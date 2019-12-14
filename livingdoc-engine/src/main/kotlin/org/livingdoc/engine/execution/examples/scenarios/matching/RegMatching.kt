package org.livingdoc.engine.execution.examples.scenarios.matching

@Suppress("NestedBlockDepth")
internal class RegMatching(
    val stepTemplate: StepTemplate,
    val step: String,
    val maxNumberOfOperations: Int,
    val lengthOfVariables: Int = 10
) {
    /**
     * cost of getting the best fitting pattern
     */
    val totalCost: Int by lazy {
        getCost()
    }

    private fun getCost(): Int {
        start()
        return operationNumber
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
    fun isMisaligned() = totalCost >= maxNumberOfOperations

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
    private var operationNumber = 0
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
                operationNumber += rematchResult.second
                operationNumber += considerVarLength(matched)
                matched = mr
            } else {
                operationNumber = maxNumberOfOperations
                matched = emptyList()
            }
            return matched
        } else {
            matched = matchedResult.destructured.toList()
            operationNumber += considerVarLength(matched)
            return matched
        }
    }

    /**
     * reconstruct the Template with its variables
     * @param templateS the template string
     * @param variables the variables in their {} brackets and the position of them
     * @return rebuilt template string
     */
    private fun reconstructVars(templateS: String, variables: Map<String, Int>): String {
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
    private fun prepareTemplateString(templateS: String): Pair<String, Map<String, Int>> {
        var s = templateS.split(" ")
        var reconString = ""
        var variableLocations = mutableMapOf<String, Int>()
        var iterat = 0
        s.forEach {

            if (checkIfVar(it)) {
                reconString += "word "
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
     * syntax:
     * preparation for stemmer algorithm
     * increasing the cost of the matching
     * @param string input string in our case it will be the step and the template
     * @return string where a/an are changed to a
     */
    private fun filterString(string: String): String {
        var outstring = ""
        var tokens = string.split(" ")

        for (i in 0..tokens.size - 1) {
            if (!checkIfVar(tokens[i])) {
                if (tokens[i].equals("a") || tokens[i].equals("an"))
                    outstring += "a "
                else
                    outstring += tokens[i] + " "
            } else
                outstring += tokens[i] + " "
        }
        outstring = StemmerHandler.cutLast(outstring).toString()

        return outstring
    }

    private fun considerVarLength(variables: List<String>): Int {
        var costIncrease = 0
        variables.forEach {
            if (it.split(" ").size >= lengthOfVariables) {

                costIncrease += it.split(" ").size - lengthOfVariables
            }
        }
        return costIncrease
    }

    /**
     * the matching function start point if there is a non stem word
     * @return the matched strings to the variables
     */
    private fun rematch(): Pair<List<String>, Int> {
        var matchingcost = 0

        // stepString
        var preppedString = StemmerHandler.stemWords(testText)
        var stepAsString = preppedString

        // template string
        var output = prepareTemplateString(templateS = templatetext)
        var sentence = output.first
        var stemmedsentence = StemmerHandler.stemWords(sentence)

        // regex matching
        var regexText = prepareTemplateToRegex(stemmedsentence, output.second)
        var matchresult = regexText.find(stepAsString)

        matchingcost++
        if (matchresult == null) {
            // matching cost increase since we used tw
            matchingcost++

            // step refinement
            val stepToStemmed = filterString(testText)
            preppedString = StemmerHandler.stemWords(stepToStemmed)
            stepAsString = preppedString

            // prepare the template string
            val sentenceToStemmed = filterString(templatetext)

            // now use refined string as input to stemmer
            output = prepareTemplateString(templateS = sentenceToStemmed)
            sentence = output.first
            stemmedsentence = StemmerHandler.stemWords(sentence)

            // regex matching
            regexText = prepareTemplateToRegex(stemmedsentence, output.second)
            matchresult = regexText.find(stepAsString)
        }

        // extend here if more algorithm have to be applied to strings or if
        // a rematch has to be made
        //

        if (matchresult != null)
            return Pair(matchresult.destructured.toList(), matchingcost)
        else return Pair(emptyList(), maxNumberOfOperations)
    }

    /**
     * extracted method for readability
     * turns a text and its variables to a regex
     * @param stemmedsentence sentence from stemmer without variables
     * @param variables variables alongside their location in the string
     * @return a regex to start comparisons
     */
    private fun prepareTemplateToRegex(stemmedsentence: String, variables: Map<String, Int>): Regex {
        val vari = variables

        var preppedTemplate = reconstructVars(templateS = stemmedsentence, variables = vari)

        val templateTxt = tokenizetemplateText(ininterntext = preppedTemplate)
        var textTemp = ""
        templateTxt.forEach {
            textTemp += it + " "
        }
        textTemp = StemmerHandler.cutLast(textTemp).toString()
        var regexText = textTemp.toRegex()

        return regexText
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
        return opencount
    }
}
