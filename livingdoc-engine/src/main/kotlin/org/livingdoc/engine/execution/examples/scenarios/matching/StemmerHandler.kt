package org.livingdoc.engine.execution.examples.scenarios.matching

import org.livingdoc.engine.algo.Stemmer

/**
 * entry point to Stemmer Algorithm, refactoring not recommended,
 * algorithm is stable and commonly used
 * initialisation point is adapted to match the RegMatching
 *
 * suppress annotation is needed.
 */
@Suppress(
    "LongMethod",
    "ComplexMethod",
    "NestedBlockDepth",
    "TooGenericExceptionCaught",
    "VariableNaming",
    "ReturnCount",
    "MaxLineLength",
    "MagicNumber",
    "ComplexCondition",
    "NewLineAtEndOfFile"
)
object StemmerHandler {

    /**
     * api function to make strings usable
     */
    fun cutLast(str: String): String? {
        var str = str
        if (str != null && str.length > 0 && str[str.length - 1] == ' ') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }
    /**
     * stem algorithm initialisaation point
     *
     * @param input the string to be looked at
     * @return The sentence stem
     */
    fun stemWords(input: String): Pair<String, Int> {
        var matchingcost = 0
        val w = CharArray(501)
        val s = Stemmer()
        var collector = ""

        var `in` = input
        var splittedin = `in`.split(" ")
        splittedin.forEach {
            var iit = it + ' '
            var iteration = 0
            while (true) {
                try {
                    var ch: Int = iit[iteration].toInt()
                    iteration++
                    // println(`in`)
                    if (Character.isLetter(ch.toChar())) {
                        var j = 0
                        while (true) {
                            ch = Character.toLowerCase(ch.toChar()).toInt()
                            w[j] = ch.toChar()
                            if (j < 500) j++
                            ch = iit[iteration].toInt()
                            iteration++
                            if (!Character.isLetter(ch.toChar())) {
                                /* to test add(char ch) */
                                for (c in 0 until j) s.add(w[c])
                                /* or, to test add(char[] w, int j) */
                                /* s.add(w, j); */
                                s.stem()

                                val u: String
                                /* and now, to test toString() : */
                                u = s.toString()
                                /* to test getResultBuffer(), getResultLength() : */
                                /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */
                                collector += u
                                break
                            }
                        }
                    }
                    if (ch < 0) break

                    collector += ch.toChar()
                } catch (e: StringIndexOutOfBoundsException) {
                    break
                }

                matchingcost += 1 / input.length
            }
        }
        return Pair(cutLast(collector).toString(), matchingcost)
    }
}
