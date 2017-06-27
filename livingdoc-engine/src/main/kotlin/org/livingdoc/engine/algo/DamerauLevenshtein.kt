package org.livingdoc.engine.algo

import java.lang.Math.min

/**
 * Calculates the Damerau-Levenshtein distance, a string similarity metric used e.g. by spell checkers.
 *
 * By default, the algorithm calculates the minimum number of four different editing operations
 * required to turn one string into another. The four operations are
 * <ul>
 *     <li><emph>insertion:</emph> a character is inserted</li>
 *     <li><emph>deletion:</emph> a character is deleted</li>
 *     <li><emph>substitution:</emph> a character is exchanged with another</li>
 *     <li><emph>transposition:</emph> two adjacent characters are swapped</li>
 * </ul>
 *
 * It is possible to specify a weight for each of these operations to tune the metric.
 *
 * The implementation is linear in space, but O(n²) in time. To avoid expensive comparisons of long
 * strings, you can specify a cutoff distance, beyond which the algorithm will abort.
 *
 * @param cutoffDistance maximum distance at which the algorithm aborts (default: no cutoff)
 * @param weightInsertion weight of an insert operation (default: 1)
 * @param weightDeletion weight of a deletion operation (default: 1)
 * @param weightSubstitution weight of a substitution operation (default: 1)
 * @param weightTransposition weight of a transposition operation (default: 1)
 */
class DamerauLevenshtein(val cutoffDistance: Int = 0,
                         val weightInsertion: Int = 1,
                         val weightDeletion: Int = 1,
                         val weightSubstitution: Int = 1,
                         val weightTransposition: Int = 1) {

    /*
     * This dynamic programming algorithm calculates a m*n matrix of distance values. The
     * final result is the distance between the two given strings a and b. The values in between
     * are locally minimal distances for the respective substrings of a and b.
     *
     *     L i v i n g D o c - b
     *    0 1 2 3 4 5 6 7 8 9
     * L  1 0 2 3 4 5 6 7 8 9
     * o  2 1 1 2 3 4 5 6 7 8
     * v  3 2 2 1 2 3 4 5 6 7
     * i  4 3 2 2 1 2 3 4 5 6
     * g  5 4 3 3 2 2 3 4 5 6
     * n  6 5 4 4 3 2 2 3 4 5
     * D  7 6 5 5 4 3 3 2 3 4
     * e  8 7 6 6 5 4 4 3 3 4
     * a  9 8 7 7 6 5 5 4 4 4
     * d 10 9 8 8 7 6 6 5 5 5 <-- distance result
     * |
     * a
     *
     * As only the last three rows are needed to calculate the next distance value, only those are kept
     * in memory.
     */

    /**
     * Calculates the editing distance between the given strings.
     */
    fun distance(a: String, b: String): Int {
        var secondToLastRow = IntArray(b.length + 1)
        var lastRow = IntArray(b.length + 1)
        var currentRow = IntArray(b.length + 1)

        for (j in 0..b.length) {
            lastRow[j] = j * weightInsertion
        }

        for (i in 0 until a.length) {
            var currentDistance = Int.MAX_VALUE
            currentRow[0] = (i + 1) * weightDeletion

            for (j in 0 until b.length) {
                currentRow[j + 1] = currentRow[j] + weightInsertion
                currentRow[j + 1] = min(currentRow[j + 1], lastRow[j + 1] + weightDeletion)
                currentRow[j + 1] = min(currentRow[j + 1], lastRow[j] + if (a[i] != b[j]) weightSubstitution else 0)
                if (i > 0 && j > 0 && a[i - 1] == b[j] && a[i] == b[j - 1]) {
                    currentRow[j + 1] = min(currentRow[j + 1], secondToLastRow[j - 1] + weightTransposition)
                }

                currentDistance = min(currentDistance, currentRow[j + 1])
            }
            // check cutoff
            if (cutoffDistance > 0 && currentDistance >= cutoffDistance) {
                return currentDistance
            }

            // rotate rows
            val tempRow = secondToLastRow
            secondToLastRow = lastRow
            lastRow = currentRow
            currentRow = tempRow
        }

        return lastRow[b.length]
    }
}