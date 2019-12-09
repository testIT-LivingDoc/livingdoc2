package org.livingdoc.engine.execution.examples.scenarios.matching

@Suppress("NestedBlockDepth")
internal class RegMatching(
    val stepTemplate: StepTemplate,
    val step: String,
    val maxCost: Int
) {
    /**
     * cost of getting the best fitting pattern
     */
    val totalCost: Int by lazy {
        getCost()
    }

    private fun getCost(): Int {
        start()
        return cost
    }

    /**
     * variable matching
     *
     */
    val variables: Map<String, String> by lazy {
        if (!isMisaligned()) getVars() else emptyMap()
    }

    private fun getVars(): Map<String, String> {
        return templatetextTokenized
    }

    // misalignment
    fun isMisaligned() = totalCost >= maxCost

    /**
     * startpoint of the Regex algorithm to match sentences
     */
    private fun start() {
        match()
    }

    // copy the input strings to local variables
    private var templatetext = stepTemplate.toString()
    private var testText = step

    // containers to store global values
    private lateinit var preparedtemplatetext: String
    private lateinit var reggedText: Regex
    private var cost = 0
    private val regularExpression = "([\\w\\s\\.\\}\\{\\P{M}\\p{M}*]+)"

    // variable to string matching container
    private var templatetextTokenized: MutableMap<String, String> = mutableMapOf()

    /**
     * matching method of the algorithm
     */
    private fun match() {
        tokenizetemplateText()
        reggedText = preparedtemplatetext.toRegex()
        val output = matchStrings()
        if (!output.isEmpty()) {
            val x = output
            var counter = 0
            templatetextTokenized.forEach {
                templatetextTokenized[it.key] = x[counter]
                counter++
            }
        }
    }

    /**
     * string matching function with a certain tolerance for typos depending on the maximum tolerance
     *
     *@return List of variables matched to the string
     */
    private fun matchStrings(): List<String> {
        var matched = emptyList<String>()

        val matchedResult = reggedText.find(testText)

        if (matchedResult == null) {
            val rematchResult = rematch()
            val mr = rematchResult.first
            if (!mr.isEmpty()) {
                cost += rematchResult.second
                matched = mr
            } else {
                cost = maxCost
                matched = emptyList()
            }
            return matched
        } else {
            matched = matchedResult.destructured.toList()
            return matched
        }
    }

    /**
     * reconstruct the Template with its variables
     * @param templateS the template string
     * @param variables the variables in their {} brackets and the position of them
     * @return rebuilt template string
     */
    private fun reconstructVars(templateS: String = templatetext, variables: Map<String, Int>): String {
        var s = templateS.split(" ")
        var reconString = ""
        var counter = 0
        s.forEach {
            var replaced = false
            variables.forEach { variable ->
                if (variable.value == counter) {
                    reconString += variable.key + " "
                    replaced = true
                }
            }
            if (!replaced) {
                reconString += it + " "
            }
            counter++
        }
        reconString = StemmerHandler.cutLast(reconString).toString()
        return reconString
    }

    /**
     * preparation of the template stirng for stemming
     * @param templateS the template string ot be prepared
     * @return the variables and their position in the template string
     */
    private fun prepareTemplateString(templateS: String = templatetext): Pair<String, Map<String, Int>> {
        var s = templateS.split(" ")
        var reconString = ""
        var variableLocations = mutableMapOf<String, Int>()
        var iterat = 0
        s.forEach {

            if (checkIfVar(it)) {
                reconString += "Word "
                variableLocations.put(it, iterat)
            } else {
                reconString += it + " "
            }
            iterat++
        }

        reconString = StemmerHandler.cutLast(reconString).toString()

        return Pair(reconString, variableLocations)
    }

    /**
     * the matching function start point if there is a non stem word
     * @return the matched strings to the variables
     */
    private fun rematch(): Pair<List<String>, Int> {
        var matchingcost = 1

        val preppedString = StemmerHandler.stemWords(testText)

        var output = prepareTemplateString(templateS = templatetext)
        var sentence = output.first
        var stemmedsentence = StemmerHandler.stemWords(sentence)
        matchingcost = stemmedsentence.second

        val vari = output.second

        var preppedTemplate = reconstructVars(templateS = stemmedsentence.first, variables = vari)

        val templateTxt = tokenizetemplateText(ininterntext = preppedTemplate)
        var textTemp = ""
        templateTxt.forEach {
            textTemp += it + " "
        }
        textTemp = StemmerHandler.cutLast(textTemp).toString()
        var regexText = textTemp.toRegex()

        val matchresult = regexText.find(preppedString.first)

        if (matchresult != null)
            return Pair(matchresult.destructured.toList(), matchingcost)
        else return Pair(emptyList(), maxCost)
    }

    /**
     * check if a variable is in a string
     *
     * @param the string to be checked
     * @return if it is a variable
     */
    private fun checkIfVar(st: String): Boolean {
        return st.contains("{") && st.contains("}")
    }

    // variables for building the regex string
    private var beforeconc = ""
    private var afterconc = ""
    private var mainstring = ""
    /**
     * getting the variables and creating the regex by replacing the "{variable}"
     * with regexes.
     * (return value not used, only for debug reasons)
     * @return a the template split into a list of strings with variables replaced by regexes
     *
     */
    private fun tokenizetemplateText(ininterntext: String = templatetext): List<String> {
        val interntext = ininterntext.split(" ").toMutableList()
        var interncounter = 0
        preparedtemplatetext = templatetext
        interntext.forEach { outer ->
            if (checkIfVar(outer)) {
                var variable = outer
                val bracketcount = countBrackets(variable.toCharArray())
                if (bracketcount == -1) {
                    return emptyList()
                }
                checkVar(variable, bracketcount)
                variable = variable.replace(beforeconc + "{", "")
                variable = variable.replace("}" + afterconc, "")

                templatetextTokenized.put(variable, "")
                preparedtemplatetext = preparedtemplatetext.replace(outer, beforeconc + regularExpression + afterconc)
                interntext[interncounter] = beforeconc + regularExpression + afterconc
            }
            interncounter++
        }
        return interntext
    }

    /**
     * filter out the variable name and set the surrounding symbols around the regex
     * builder for the regex
     *
     * @param variable the input variable the be checked
     * @param bracketcount the number of brackets counted in before
     *
     */
    private fun checkVar(variable: String, bracketcount: Int) {
        var open: Int = bracketcount
        var closed: Int = bracketcount

        var afterstring = false
        var beforestring = true

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
    }

    /**
     * simple counter method to count number of brackets
     * preparation for the variable check afterwards
     *
     * @param input the string to be checked as chararray
     * @return number of brackets found, -1 if the string is not a variable
     */
    private fun countBrackets(input: CharArray): Int {
        var opencount = 0
        var closecount = 0
        input.forEach {
            if (it.equals('{')) {
                opencount++
            }
            if (it.equals('}')) {
                closecount++
            }
        }
        if ((opencount == 0 && closecount == 0) || opencount != closecount)
            return -1
        return opencount
    }
}
