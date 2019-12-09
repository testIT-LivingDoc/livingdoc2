package org.livingdoc.engine.algo

/**
 * Stemmer, implementing the Porter Stemming Algorithm
 *
 * The Stemmer class transforms a word into its root form.  The input
 * word can be provided a character at time (by calling add()), or at once
 * by calling one of the various stem(something) methods.
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
class Stemmer {

    private val INC = 50

    /**
     * Returns a reference to a character buffer containing the results of
     * the stemming process.  You also need to consult getResultLength()
     * to determine the length of the result.
     */
    private var resultBuffer: CharArray
        private set
    private var i: Int
    /**
     * Returns the length of the word resulting from the stemming process.
     */
    /* offset into b */
    private var resultLength: Int
        private set
    /* offset to end of stemmed word */
    private var j = 0
    private var k = 0
    /**
     * Add a character to the word being stemmed.  When you are finished
     * adding characters, you can call stem(void) to stem the word.
     */
    fun add(ch: Char) {
        if (i == resultBuffer.size) {
            val new_b = CharArray(i + INC)
            for (c in 0 until i) new_b[c] = resultBuffer[c]
            resultBuffer = new_b
        }
        resultBuffer[i++] = ch
    }

    /** Adds wLen characters to the word being stemmed contained in a portion
     * of a char[] array. This is like repeated calls of add(char ch), but
     * faster.
     */
    fun add(w: CharArray, wLen: Int) {
        if (i + wLen >= resultBuffer.size) {
            val new_b = CharArray(i + wLen + INC)
            for (c in 0 until i) new_b[c] = resultBuffer[c]
            resultBuffer = new_b
        }
        for (c in 0 until wLen) resultBuffer[i++] = w[c]
    }

    /**
     * After a word has been stemmed, it can be retrieved by toString(),
     * or a reference to the internal buffer can be retrieved by getResultBuffer
     * and getResultLength (which is generally more efficient.)
     */
    override fun toString(): String {
        return String(resultBuffer, 0, resultLength)
    }

    /* cons(i) is true <=> b[i] is a consonant. */
    private fun cons(i: Int): Boolean {
        return when (resultBuffer[i]) {
            'a', 'e', 'i', 'o', 'u' -> false
            'y' -> if (i == 0) true else !cons(i - 1)
            else -> true
        }
    }

    /* m() measures the number of consonant sequences between 0 and j. if c is
      a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
      presence,

         <c><v>       gives 0
         <c>vc<v>     gives 1
         <c>vcvc<v>   gives 2
         <c>vcvcvc<v> gives 3
         ....
   */
    private fun m(): Int {
        var n = 0
        var i = 0
        while (true) {
            if (i > j) return n
            if (!cons(i)) break
            i++
        }
        i++
        while (true) {
            while (true) {
                if (i > j) return n
                if (cons(i)) break
                i++
            }
            i++
            n++
            while (true) {
                if (i > j) return n
                if (!cons(i)) break
                i++
            }
            i++
        }
    }

    /* vowelinstem() is true <=> 0,...j contains a vowel */
    private fun vowelinstem(): Boolean {
        var i: Int
        i = 0
        while (i <= j) {
            if (!cons(i)) return true
            i++
        }
        return false
    }

    /* doublec(j) is true <=> j,(j-1) contain a double consonant. */
    private fun doublec(j: Int): Boolean {
        if (j < 1) return false
        return if (resultBuffer[j] != resultBuffer[j - 1]) false else cons(j)
    }

    /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
      and also if the second c is not w,x or y. this is used when trying to
      restore an e at the end of a short word. e.g.

         cav(e), lov(e), hop(e), crim(e), but
         snow, box, tray.

   */
    private fun cvc(i: Int): Boolean {
        if (i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) return false
        run {
            val ch = resultBuffer[i].toInt()
            if (ch == 'w'.toInt() || ch == 'x'.toInt() || ch == 'y'.toInt()) return false
        }
        return true
    }

    private fun ends(s: String): Boolean {
        val l = s.length
        val o = k - l + 1
        if (o < 0) return false
        for (i in 0 until l) if (resultBuffer[o + i] != s[i]) return false
        j = k - l
        return true
    }

    /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
      k. */
    private fun setto(s: String) {
        val l = s.length
        val o = j + 1
        for (i in 0 until l) resultBuffer[o + i] = s[i]
        k = j + l
    }

    /* r(s) is used further down. */
    private fun r(s: String) {
        if (m() > 0) setto(s)
    }

    /* step1() gets rid of plurals and -ed or -ing. e.g.

          caresses  ->  caress
          ponies    ->  poni
          ties      ->  ti
          caress    ->  caress
          cats      ->  cat

          feed      ->  feed
          agreed    ->  agree
          disabled  ->  disable

          matting   ->  mat
          mating    ->  mate
          meeting   ->  meet
          milling   ->  mill
          messing   ->  mess

          meetings  ->  meet

   */
    private fun step1() {
        if (resultBuffer[k] == 's') {
            if (ends("sses")) k -= 2 else if (ends("ies")) setto("i") else if (resultBuffer[k - 1] != 's') k--
        }
        if (ends("eed")) {
            if (m() > 0) k--
        } else if ((ends("ed") || ends("ing")) && vowelinstem()) {
            k = j
            if (ends("at")) setto("ate") else if (ends("bl")) setto("ble") else if (ends("iz")) setto("ize") else if (doublec(
                    k
                )
            ) {
                k--
                run {
                    val ch = resultBuffer[k].toInt()
                    if (ch == 'l'.toInt() || ch == 's'.toInt() || ch == 'z'.toInt()) k++
                }
            } else if (m() == 1 && cvc(k)) setto("e")
        }
    }

    /* step2() turns terminal y to i when there is another vowel in the stem. */
    private fun step2() {
        if (ends("y") && vowelinstem()) resultBuffer[k] = 'i'
    }

    /* step3() maps double suffices to single ones. so -ization ( = -ize plus
      -ation) maps to -ize etc. note that the string before the suffix must give
      m() > 0. */
    private fun step3() {
        if (k == 0) return /* For Bug 1 */
        when (resultBuffer[k - 1]) {
            'a' -> {
                if (ends("ational")) {
                    r("ate")
                }
                if (ends("tional")) {
                    r("tion")
                }
            }
            'c' -> {
                if (ends("enci")) {
                    r("ence")
                }
                if (ends("anci")) {
                    r("ance")
                }
            }
            'e' -> if (ends("izer")) {
                r("ize")
            }
            'l' -> {
                if (ends("bli")) {
                    r("ble")
                }
                if (ends("alli")) {
                    r("al")
                }
                if (ends("entli")) {
                    r("ent")
                }
                if (ends("eli")) {
                    r("e")
                }
                if (ends("ousli")) {
                    r("ous")
                }
            }
            'o' -> {
                if (ends("ization")) {
                    r("ize")
                }
                if (ends("ation")) {
                    r("ate")
                }
                if (ends("ator")) {
                    r("ate")
                }
            }
            's' -> {
                if (ends("alism")) {
                    r("al")
                }
                if (ends("iveness")) {
                    r("ive")
                }
                if (ends("fulness")) {
                    r("ful")
                }
                if (ends("ousness")) {
                    r("ous")
                }
            }
            't' -> {
                if (ends("aliti")) {
                    r("al")
                }
                if (ends("iviti")) {
                    r("ive")
                }
                if (ends("biliti")) {
                    r("ble")
                }
            }
            'g' -> if (ends("logi")) {
                r("log")
            }
        }
    }

    /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
    private fun step4() {
        when (resultBuffer[k]) {
            'e' -> {
                if (ends("icate")) {
                    r("ic")
                }
                if (ends("ative")) {
                    r("")
                }
                if (ends("alize")) {
                    r("al")
                }
            }
            'i' -> if (ends("iciti")) {
                r("ic")
            }
            'l' -> {
                if (ends("ical")) {
                    r("ic")
                }
                if (ends("ful")) {
                    r("")
                }
            }
            's' -> if (ends("ness")) {
                r("")
            }
        }
    }

    /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
    private fun step5() {
        if (k == 0) return /* for Bug 1 */
        when (resultBuffer[k - 1]) {
            'a' -> {
                if (ends("al"))
                    return
            }
            'c' -> {
                if (ends("ance"))
                    if (ends("ence"))
                        return
            }
            'e' -> {
                if (ends("er"))
                    return
            }
            'i' -> {
                if (ends("ic"))
                    return
            }
            'l' -> {
                if (ends("able"))
                    if (ends("ible"))
                        return
            }
            'n' -> {
                if (ends("ant"))
                    if (ends("ement"))
                        if (ends("ment"))
                        /* element etc. not stripped before the m */ if (ends("ent"))
                            return
            }
            'o' -> {
                if (ends("ion") && j >= 0 && (resultBuffer[j] == 's' || resultBuffer[j] == 't'))
                /* j >= 0 fixes Bug 2 */ if (ends("ou"))
                    return
            }
            's' -> {
                if (ends("ism"))
                    return
            }
            't' -> {
                if (ends("ate"))
                    if (ends("iti"))
                        return
            }
            'u' -> {
                if (ends("ous"))
                    return
            }
            'v' -> {
                if (ends("ive"))
                    return
            }
            'z' -> {
                if (ends("ize"))
                    return
            }
            else -> return
        }
        if (m() > 1) k = j
    }

    /* step6() removes a final -e if m() > 1. */
    private fun step6() {
        j = k
        if (resultBuffer[k] == 'e') {
            val a = m()
            if (a > 1 || a == 1 && !cvc(k - 1)) k--
        }
        if (resultBuffer[k] == 'l' && doublec(k) && m() > 1) k--
    }

    /** Stem the word placed into the Stemmer buffer through calls to add().
     * Returns true if the stemming process resulted in a word different
     * from the input.  You can retrieve the result with
     * getResultLength()/getResultBuffer() or toString().
     */
    fun stem() {
        k = i - 1
        if (k > 1) {
            step1()
            step2()
            step3()
            step4()
            step5()
            step6()
        }
        resultLength = k + 1
        i = 0
    }

    /* unit of size whereby b is increased */
    init {
        resultBuffer = CharArray(INC)
        i = 0
        resultLength = 0
    }
}
