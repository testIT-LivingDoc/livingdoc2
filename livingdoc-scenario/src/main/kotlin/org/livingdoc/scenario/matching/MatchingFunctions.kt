package org.livingdoc.scenario.matching

/**
 * All functions used to perform the regex Matching
 */
object MatchingFunctions {

    /**
     * regex when not at then end of the template
     */
    private val regularExpression = "(.*\\s|)"
    /**
     * regex when at the end of the template
     */
    private val endOfStringRegex = "(\\s.*|)"
    /**
     * regex when there are symbols before/after the variable in the template
     */
    private val allRegex = "(.*)"

    /**
     * characters used by regex that need to be escaped
     */
    private val regexCharacters =
        listOf('.', '*', '+', '^', '-', '\\', '|', '$', '&', '(', ')', '/', '[', ']', '?', '{', '}')

    /**
     * change all "an" to "a"
     * escape all regex symbols unless it is a variable or a step string
     * @param string the template string
     * @return prepared string to be used for precalculations for regex
     */
    fun filterString(string: String, isStep: Boolean): String {
        val tokens = string.split(" ")
        val out = tokens.map {
            if (it.equals("an")) "a"
            else if (isStep || checkIfVar(it)) it
            else it.map { character ->
                if (regexCharacters.contains(character)) "\\$character"
                else "$character"
            }.joinToString("")
        }
        val outputString = out.joinToString(" ")
        return outputString
    }

    /**
     * function to consider the length of each variable
     * cost is added if variable length is too high
     * @return the increase of cost
     */
    fun considerVarLength(templatetextTokenized: Map<String, String>): Float {
        if (templatetextTokenized.isNotEmpty()) {
            val sum = templatetextTokenized.values.sumBy { string -> string.length }.toFloat()
            return sum / templatetextTokenized.size
        } else
            return 0.0f
    }

    /**
     * Postfiltering the blank out of the variable value
     * Regex accepted one more blank than needed
     * @return Variable value
     */
    fun variablevaluePostcalc(it: String): String {
        var variableValue = it
        // look at the string with condition of being not empty
        if (it.toCharArray().isNotEmpty()) {
            // last one is a blank or first one is a blank
            if (it.toCharArray().last().equals(' ')) {
                variableValue = it.toCharArray().dropLast(1).joinToString("")
            } else if (it.toCharArray().first().equals(' ')) {
                variableValue = it.toCharArray().drop(1).joinToString("")
            }
        }
        return variableValue
    }

    /**
     * check if a variable is in a string
     *
     * @param st the string to be checked
     * @return if it is a variable
     */
    fun checkIfVar(st: String): Boolean {
        return st.contains("{") && st.contains("}")
    }

    /**
     * getting the variables and creating the regex by replacing the "{variable}"
     * with regexes.
     *
     * variable is in final two strings
     * case 1: "string string {variable} string" & "string string {variable}"
     *
     * variable is at the beginning or at any other position
     * case 2: "string {variable} string string"
     *
     * the template has only one string as component
     * case 3: "{variable}" & "string"
     *
     * @param value the input string(step template)
     *
     * @return Pair of regex as string and the map with the variable names
     *
     */
    fun templateStepToRegexString(value: String): Pair<String, List<String>> {
        var interntext = value.split(" ")
        val variablesMap = mutableListOf<String>()

        var singlestring = true
        var lineend = ""

        var lastentry = ""
        var secondlast = ""
        // if the template is longer than one string
        if (interntext.size >= 2) {
            // handle last two strings separately
            lastentry = interntext.last()
            secondlast = interntext[interntext.size - 2]

            singlestring = false
            lineend = " "
            // for the rest do a mapping
            interntext = interntext.dropLast(2)
        }
        val outmap = interntext.map {
            if (checkIfVar(it)) {
                val bracketcount =
                    countBrackets(it.toCharArray())
                val (before, variable, after) = constructVar(
                    it,
                    bracketcount
                )

                variablesMap.add(variable)

                getRegex(
                    before,
                    after,
                    false,
                    singlestring
                )
            } else {
                it + lineend
            }
        }
        val outstring = outmap.joinToString("")
        // consider last two strings and put all together
        if (singlestring)
            return Pair(outstring, variablesMap)
        return computeRegexAtEndOfString(
            outstring,
            lastentry,
            secondlast,
            variablesMap
        )
    }

    /**
     * the final two strings in a template have to be looked at separately
     * there are three cases
     *
     * {variable} string
     * and
     * string {variable}
     * and
     * string string
     *
     * because of an empty string as input the blanks in the strings above have to be
     * handled outside of the main string
     *
     * output will be regex string and the variable added to the variables map
     *
     * @return Pair of the regex string and the map with variables
     */
    private fun computeRegexAtEndOfString(
        outstring: String,
        lastentry: String,
        secondlast: String,
        variablesMap: List<String>
    ): Pair<String, List<String>> {
        var inneroutstring = outstring
        val innerVariableMap = variablesMap.toMutableList()
        // check last one and add them to the outmap
        if (checkIfVar(lastentry)) {
            val bracketcount =
                countBrackets(lastentry.toCharArray())
            val (before, variable, after) = constructVar(
                lastentry,
                bracketcount
            )
            // adding the varibale name
            innerVariableMap.add(variable)
            // rebuilding the regex string
            inneroutstring += secondlast + before +
                    getRegex(
                        before,
                        after,
                        true,
                        false
                    ) + after
        } else if (checkIfVar(secondlast)) {
            val bracketcount =
                countBrackets(secondlast.toCharArray())
            val (before, variable, after) = constructVar(
                secondlast,
                bracketcount
            )
            innerVariableMap.add(variable)
            inneroutstring +=
                getRegex(
                    before,
                    after,
                    false,
                    false
                ) + lastentry
        } else {
            inneroutstring += secondlast + " " + lastentry
        }
        return Pair(inneroutstring, innerVariableMap)
    }

    /**
     * filter out the variable name and set the surrounding symbols around the regex
     * builder for the regex
     *
     * @param variable the input variable the be checked
     * @param bracketcount the number of brackets counted in before
     * @return a triple containing the string before, after and inside brackets
     */
    private fun constructVar(variable: String, bracketcount: Int): Triple<String, String, String> {
        var open: Int = bracketcount
        var closed: Int = bracketcount

        var afterstring = false
        var beforestring = true

        var mainstring = ""
        var beforeconc = ""
        var afterconc = ""

        variable.forEach {
            // order MUST NOT be changed
            if (afterstring)
                afterconc += it

            if (it.equals('}')) {
                if (closed == 1)
                    afterstring = true
                closed--
            }

            // the main string
            if (!beforestring && !afterstring)
                mainstring += it

            // the before string
            if (it.equals('{')) {
                open--
                beforestring = false
            }
            if (beforestring)
                beforeconc += it
        }

        return Triple(beforeconc, mainstring, afterconc)
    }

    /**
     * simple counter method to count number of brackets
     * preparation for the variable check afterwards
     *
     * opening bracket count must be equal to the closing count
     * defined in steptemplate
     * @param input the string to be checked as chararray
     * @return number of brackets found
     */
    private fun countBrackets(input: CharArray): Int {
        return input.count { "{".contains(it) }
    }

    /**
     * depending on situation substitute a regex
     *
     * some text {variable} -> some text(regex)
     * some {variable} text text -> some (regex)text text
     * {variable} some text -> (regex)some text
     * some{variable}text -> some(regex)text
     *
     * @param before string immediately in front of the brackets
     * @param after string immedietaly after the brackets
     * @param stringend if we are at the end of the string
     * @return the substituted regex
     *
     */
    private fun getRegex(before: String, after: String, stringend: Boolean, singlestring: Boolean): String {
        if (singlestring) {
            return before + allRegex + after
        }

        var regexValue = ""
        // depending whether there are symbols right before/after the brackets of the variable
        if (before.equals("") && after.equals("")) {
            // are we at the end of a string
            if (stringend)
                regexValue = endOfStringRegex
            else
                regexValue = regularExpression
        } else {
            // are we at the end of a string
            if (stringend)
                regexValue = before + allRegex + after
            else
                regexValue = before + allRegex + after + " "
        }
        return regexValue
    }
}
