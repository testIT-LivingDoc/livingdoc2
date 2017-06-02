package org.livingdoc.engine.executor.scenario

import java.lang.Math.min


/**
 * An alignment of a scenario step with a `StepTemplate`. Aligning a scenario step with a template allows us to check
 * if the template matches the step and to extract any variables.
 *
 * Example: The following alignment of a perfectly matching template and scenario step
 * ```
 *         {username} has {action} the {object}.
 *         -----Peter has ----left the building.
 * ```
 * yields an editing distance of `0` (all Text fragments of the `StepTemplate` are matched without modification)
 * and the variables `username = "Peter"`, `action = "left"` and `object = "building"`.
 *
 * If a scenario step does not align well, it is an unlikely match. Because alignment calculation is expensive,
 * it will be aborted when the distance between the `StepTemplate` and the scenario step exceeds `maxDistance`.
 */
internal class Alignment(
        val stepTemplate: StepTemplate,
        val step: String,
        val maxDistance: Int) {

    /**
     * The Levenshtein distance between the Text fragments of the `StepTemplate` and the aligned scenario step.
     */
    val distance: Int by lazy {
        distanceMatrix.last().last()
    }

    fun isMisaligned() = distance >= maxDistance

    val variables: Map<String, String> by lazy {
        if (!isMisaligned()) extractVariables() else emptyMap()
    }

    /**
     * A pair of strings representing the alignment of the StepTemplate and the scenario steps.
     */
    val alignedStrings: Pair<String, String> by lazy {
        if (!isMisaligned()) buildAlignedStrings() else Pair(stepTemplate.toString(), step)
    }

    private val distanceMatrix: DistanceMatrix by lazy {
        constructDistanceMatrix(stepTemplate, step)
    }

    private val costInsertion = 1
    private val costDeletion = 1
    private val costSubstitution = 1
    private val costVariableExpansion = 0
    private val costVariableDeletion = 0

    /**
     * Calculates the distance matrix. Think of this as putting the `StepTemplate` and the scenario step next to
     * each other and judging how well the template fits to the step.
     *
     * To calculate an optimal global alignment of the `StepTemplate` and the String `s` representing the scenario step,
     * we use an adapted version of the Needleman-Wunsch algorithm. The algorithm runs in to phases:
     *
     * 1. construct a distance matrix
     * 2. trace an optimal path back through the matrix to the origin
     *
     * The following shows the first part of a distance matrix for the template "User {name} has entered the building."
     * and the scenario step "User Peter has entered the building."
     *
     * ```
     *         U  s  e  r     P  e  t  e  r     h  a  s    ... -- s
     *      0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
     * U    1  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14
     * s    2  1  0  1  2  3  4  5  6  7  8  9 10 11 12 13
     * e    3  2  1  0  1  2  3  4  5  6  7  8  9 10 11 12
     * r    4  3  2  1  0  1  2  3  4  5  6  7  8  9 10 11
     *      5  4  3  2  1  0  1  2  3  4  5  6  7  8  9 10
     * name 5  4  3  2  1  0  0  0  0  0  0  0  0  0  0  0
     *      6  5  4  3  2  1  1  1  1  1  1  0  1  1  1  0
     * h    7  6  5  4  3  2  2  2  2  2  2  1  0  1  2  1
     * a    8  7  6  5  4  3  3  3  3  3  3  2  1  0  1  2
     * s    9  8  7  6  5  4  4  4  4  4  4  3  2  1  0  1
     *     10  9  8  7  6  5  5  5  5  5  5  4  3  2  1  0
     * ...
     * |
     * stepTemplate("User ", name, " has entered the building.")
     * ```
     *
     * The matrix tracks the Levenshtein distance for all pairs of substrings of the `StepTemplate` and `s`. Characters
     * aligned with a variable do not count towards that distance.
     *
     * Distance grow monotonically from the top-left origin to the bottom-right. No row can contain a distance smaller
     * than the smallest value of the previous row. Because calculating the entire matrix is O(n²) in time, we can abort
     * the calculation, once the minimum value in a row exceeds `maxDistance` and it becomes obvious that the template
     * does not match the scenario step.
     */
    private fun constructDistanceMatrix(stepTemplate: StepTemplate, s: String): DistanceMatrix {
        val d = Array(stepTemplate.length() + 1) { IntArray(s.length + 1) }

        for (j in 0..s.length) {
            d[0][j] = j * costInsertion
        }

        var offset = 1
        for (fragment in stepTemplate.fragments) {
            for (i in offset..offset + length(fragment) - 1) {
                when(fragment) {
                    is Text -> {
                        d[i][0] = d[i - 1][0] + costDeletion
                        var currentMinDistance = d[i][0]
                        for (j in 1..s.length) {
                            d[i][j] = d[i][j - 1] + costInsertion
                            d[i][j] = min(d[i][j], d[i - 1][j] + costDeletion)
                            d[i][j] = min(d[i][j], d[i - 1][j - 1]
                                    + if (fragment.content[i - offset] != s[j - 1]) costSubstitution else 0)
                            currentMinDistance = min(currentMinDistance, d[i][j])
                        }
                        if (currentMinDistance > maxDistance) {
                            d[d.lastIndex][s.length] = currentMinDistance
                            return d
                        }
                    }
                    is Variable -> {
                        d[offset][0] = d[offset - 1][0] + costVariableDeletion
                        for (j in 1..s.length) {
                            d[offset][j] = d[offset - 1][j] + costVariableDeletion
                            d[offset][j] = min(d[offset][j], d[offset][j - 1] + costVariableExpansion)
                            d[offset][j] = min(d[offset][j], d[offset - 1][j - 1] + costVariableExpansion)
                        }
                    }
                }
            }
            offset += length(fragment)
        }

        return d
    }

    /**
     * Extracts the variables from the scenario step and returns them as a map of variable names to their respective
     * values.
     */
    private fun extractVariables(): Map<String, String> {
        val variables = mutableMapOf<String, String>()
        var currentValue = ""

        backtrace(object : TracebackHandler {
            override fun handleMatchOrSubstitution(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                if (fragment is Variable) {
                    currentValue = step[stepIndex] + currentValue
                    variables[fragment.name] = currentValue
                } else {
                    currentValue = ""
                }
            }

            override fun handleInsertion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                if (fragment is Variable) {
                    currentValue = step[stepIndex] + currentValue
                }
            }

            override fun handleDeletion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                if (fragment is Variable) {
                    variables[fragment.name] = currentValue
                } else {
                    currentValue = ""
                }
            }
        })

        return variables
    }

    /**
     * Returns a pair of strings representing the alignment of the `StepTemplate` and the scenario step. Useful for
     * debugging and error messages.
     */
    private fun buildAlignedStrings(): Pair<String, String> {
        var alignedTemplate = ""
        var alignedString = ""

        backtrace(object : TracebackHandler {
            override fun handleMatchOrSubstitution(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                alignedTemplate = fragmentChar(fragment, fragmentIndex) + alignedTemplate
                alignedString = step[stepIndex] + alignedString
            }

            override fun handleInsertion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                val gap = if (fragment is Text) '-' else 'X'
                alignedTemplate = gap + alignedTemplate
                alignedString = step[stepIndex] + alignedString
            }

            override fun handleDeletion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int) {
                alignedTemplate = fragmentChar(fragment, fragmentIndex) + alignedTemplate
                alignedString = "-" + alignedString
            }

            private fun fragmentChar(fragment: Fragment, index: Int): Char {
                return if (fragment is Text) fragment.content[index] else 'X'
            }
        })

        return Pair(alignedTemplate, alignedString)
    }

    private interface TracebackHandler {
        fun handleMatchOrSubstitution(fragment: Fragment, fragmentIndex: Int, stepIndex: Int)
        fun handleInsertion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int)
        fun handleDeletion(fragment: Fragment, fragmentIndex: Int, stepIndex: Int)
    }

    /**
     * Performs the second phase of the Needleman-Wunsch algorithm: tracing back a path through the distance
     * matrix to determine an optimal global alignment. Such a path follows the smallest Levenshtein distance
     * back to the origin:
     *
     * ```
     *         U  s  e  r     P  e  t  e  r     h  a  s    ... -- s
     *      0
     * U       0
     * s          0
     * e             0
     * r                0
     *                     0
     * name                   0  0  0  0  0
     *                                       0
     * h                                        0
     * a                                           0
     * s                                              0
     *                                                   0
     * ...
     * |
     * stepTemplate("User ", name, " has entered the building.")
     * ```
     *
     * Each step along that path corresponds to an editing action (match or substitution, insertion, deletion). It falls
     * to the supplied handler to decide how each operation is processed.
     */
    private fun backtrace(handler: TracebackHandler) {
        var offset = stepTemplate.length()
        var i = offset
        var j = step.length
        for (fragment in stepTemplate.fragments.reversed()) {
            offset -= length(fragment)
            while (i > offset || (i == 0 && j > 0)) {
                if (i > offset && j > 0
                        && distanceMatrix[i - 1][j - 1] <= distanceMatrix[i - 1][j]
                        && distanceMatrix[i - 1][j - 1] <= distanceMatrix[i][j - 1]) {
                    --i; --j
                    handler.handleMatchOrSubstitution(fragment, i - offset, j)
                } else if (i > offset && (j == 0 || distanceMatrix[i - 1][j] <= distanceMatrix[i][j - 1])) {
                    --i
                    handler.handleDeletion(fragment, i - offset, j)
                } else {
                    assert(j > 0)
                    --j
                    handler.handleInsertion(fragment, i - offset, j)
                }
            }
        }
    }
}

typealias DistanceMatrix = Array<IntArray>

private fun StepTemplate.length() = fragments.map { length(it) }.sum()

private fun length(fragment: Fragment): Int = when(fragment) {
    is Text -> fragment.content.length
    else -> 1
}

