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
    "ComplexMethod",
    "NestedBlockDepth",
    "MagicNumber"
)
object StemmerHandler {
    /**
     * stem algorithm initialisation point
     *
     * @param input the string to be looked at
     * @return The sentence stem
     */
    fun stemWords(input: String): String {

        val w = CharArray(501)
        val s = Stemmer()
        val collector = StringBuffer()

        val splitIn = input.split(" ")
        splitIn.forEach {
            val iit = "$it "
            var iteration = 0
            while (true) {
                try {
                    var ch: Int = iit[iteration].toInt()
                    iteration++

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

                                /* and now, to test toString() : */
                                val u: String = s.toString()
                                /* to test getResultBuffer(), getResultLength() : */
                                /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */
                                collector.append(u)
                                break
                            }
                        }
                    }
                    if (ch < 0) break
                    collector.append(ch.toChar())
                } catch (e: StringIndexOutOfBoundsException) {
                    break
                }
            }
        }
        return collector.toString()
    }
}
