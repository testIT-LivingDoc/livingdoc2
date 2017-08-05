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

    /* To calculate an optimal global alignment of the `StepTemplate` and the String `s` representing the scenario step,
     * we use an adapted version of the Needleman-Wunsch algorithm. The algorithm runs in two phases:
     *
     * 1. construct a distance matrix
     * 2. trace an optimal path back through the matrix to the origin
     */

    /**
     * The editing distance between the Text fragments of the `StepTemplate` and the aligned scenario step.
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

    val distanceMatrix: DistanceMatrix by lazy {
        constructDistanceMatrix(stepTemplate, step)
    }

    /**
     * Calculates the distance matrix. Think of this as putting the `StepTemplate` and the scenario step next to
     * each other and judging how well the template fits to the step.
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
     * The matrix tracks the editing distance for all pairs of substrings of the `StepTemplate` and `s`. Characters
     * aligned with a variable do not count towards that distance.
     *
     * Distance grows monotonically from the top-left origin to the bottom-right. No row can contain a distance smaller
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
                when (fragment) {
                    is Text -> {
                        d[i][0] = d[i - 1][0] + costDeletion(fragment.content[i - offset])
                        var currentMinDistance = d[i][0]
                        for (j in 1..s.length) {
                            d[i][j] = d[i][j - 1] + costInsertion
                            d[i][j] = min(d[i][j], d[i - 1][j] + costDeletion(fragment.content[i - offset]))
                            d[i][j] = min(d[i][j], d[i - 1][j - 1] + costSubstitution(fragment.content[i - offset], s[j - 1]))
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
                            d[offset][j] = min(d[offset][j], d[offset][j - 1] + costVariableExtension(s[j - 1]))
                            d[offset][j] = min(d[offset][j], d[offset - 1][j - 1] + costVariableExtension(s[j - 1]))
                        }
                    }
                }
            }
            offset += length(fragment)
        }

        return d
    }


    /* The following values define how we calculate the editing distance between template and step. */

    private val costInsertion = 1
    private val costSimpleDeletion = 1
    private val costDeletedQuotationChar = maxDistance

    private fun costDeletion(deletedCharInFragment: Char): Int {
        return if (stepTemplate.quotationCharacters.contains(deletedCharInFragment))
            costDeletedQuotationChar
        else
            costSimpleDeletion
    }

    private val costMatchingCharacters = 0
    private val costSimpleSubstitution = 1
    private val costSubstitutedQuotationChar = maxDistance

    private fun costSubstitution(inFragment: Char, inStep: Char): Int {
        return if (inFragment != inStep) {
            if (stepTemplate.quotationCharacters.contains(inFragment))
                costSubstitutedQuotationChar
            else
                costSimpleSubstitution
        } else
            costMatchingCharacters
    }

    private val costVariableSimpleExtension = 0
    private val costVariableExtensionOverQuotationChar = maxDistance

    private fun costVariableExtension(extension: Char): Int {
        return if (stepTemplate.quotationCharacters.contains(extension))
            costVariableExtensionOverQuotationChar
        else
            costVariableSimpleExtension
    }

    private val costVariableDeletion = 0

    /**
     * Extracts the variables from the scenario step and returns them as a map of variable names to their respective
     * values.
     */
    private fun extractVariables(): Map<String, String> {
        val variables = mutableMapOf<String, String>()
        var currentValue = ""

        backtrace(
                onMatchOrSubstitution = { fragment, _, stepIndex ->
                    if (fragment is Variable) {
                        currentValue = step[stepIndex] + currentValue
                        variables[fragment.name] = currentValue
                    } else {
                        currentValue = ""
                    }
                },
                onInsertion = { fragment, _, stepIndex ->
                    if (fragment is Variable) {
                        currentValue = step[stepIndex] + currentValue
                    }
                },
                onDeletion = { fragment, _, _ ->
                    if (fragment is Variable) {
                        variables[fragment.name] = currentValue
                    } else {
                        currentValue = ""
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

        backtrace(
                onMatchOrSubstitution = { fragment, fragmentIndex, stepIndex ->
                    val fragmentChar = if (fragment is Text) fragment.content[fragmentIndex] else 'X'
                    alignedTemplate = fragmentChar + alignedTemplate
                    alignedString = step[stepIndex] + alignedString
                },
                onInsertion = { fragment, _, stepIndex ->
                    val gap = if (fragment is Text) '-' else 'X'
                    alignedTemplate = gap + alignedTemplate
                    alignedString = step[stepIndex] + alignedString
                },
                onDeletion = { fragment, fragmentIndex, _ ->
                    val fragmentChar = if (fragment is Text) fragment.content[fragmentIndex] else 'X'
                    alignedTemplate = fragmentChar + alignedTemplate
                    alignedString = "-" + alignedString
                })

        return Pair(alignedTemplate, alignedString)
    }

    /**
     * Performs the second phase of the Needleman-Wunsch algorithm: tracing back a path through the distance
     * matrix to determine an optimal global alignment. Such a path follows the smallest editing distance
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
    private fun backtrace(onMatchOrSubstitution: BacktraceHandler, onInsertion: BacktraceHandler, onDeletion: BacktraceHandler) {
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
                    onMatchOrSubstitution(fragment, i - offset, j)
                } else if (i > offset && (j == 0 || distanceMatrix[i - 1][j] <= distanceMatrix[i][j - 1])) {
                    --i
                    onDeletion(fragment, i - offset, j)
                } else {
                    assert(j > 0)
                    --j
                    onInsertion(fragment, i - offset, j)
                }
            }
        }
    }
}

internal typealias DistanceMatrix = Array<IntArray>

private typealias BacktraceHandler = (fragment: Fragment, fragmentIndex: Int, stepIndex: Int) -> Unit

private fun StepTemplate.length() = fragments.map { length(it) }.sum()

private fun length(fragment: Fragment): Int = when (fragment) {
    is Text -> fragment.content.length
    else -> 1
}

