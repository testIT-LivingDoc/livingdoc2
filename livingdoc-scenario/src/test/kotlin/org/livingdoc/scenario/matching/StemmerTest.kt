package org.livingdoc.scenario.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class StemmerTest {
    private val input = "this modified String is a test string"
    private val input2 = "The user buys an apple"

    @Test
    fun `test add function`() {
        val s = Stemmer()
        val chars: CharArray = "asdfghjkl".toCharArray()
        s.add(chars, chars.size)

        Assertions.assertThat(s.toString()).isEqualTo("")
    }

    @Test
    fun `max chararray size`() {
        val s = Stemmer()
        var testingstring = ""
        for (a in 0..52)
            testingstring += a.toString()
        val chars = testingstring.toCharArray()
        s.add(chars, chars.size)

        Assertions.assertThat(s.toString()).isEqualTo("")
    }

    @Test
    fun `testing special cases for stemmer`() {
        val w = CharArray(501)
        val s = Stemmer()
        var collector = ""

        var `in` = input2
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
                                // print(u)
                                collector += u

                                break
                            }
                        }
                    }
                    if (ch < 0) break
                    // print(ch.toChar())
                    collector += ch.toChar()
                } catch (e: NullPointerException) {
                    break
                } catch (e: StringIndexOutOfBoundsException) {
                    break
                }
            }
        }
        Assertions.assertThat(collector).isEqualTo("the user bui an appl ")
    }

    @Test
    fun `testing stemmer algorithm`() {
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
                                // print(u)
                                collector += u

                                break
                            }
                        }
                    }
                    if (ch < 0) break
                    // print(ch.toChar())
                    collector += ch.toChar()
                } catch (e: NullPointerException) {
                    break
                } catch (e: StringIndexOutOfBoundsException) {
                    break
                }
            }
        }
        Assertions.assertThat(collector).isEqualTo("thi modifi string is a test string ")
    }
}
