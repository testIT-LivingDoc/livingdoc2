package org.livingdoc.scenario.matching

/**
 * RegMatching is used to match a step to a template and give them a cost depending on the
 * similarity of the strings.
 *
 * If the template does not match at all the variables consists of an empty list.
 * The cost is the maximum possible cost(maxNumberOfOperations).
 *
 * The cost consists of the number of operations on the step/the template.
 * Additionally length of the strings in each variable is also considered into the cost.
 *
 * If the template and the step can be matched initially,
 * the cost only considers the length of the strings in each variable.
 * Else there will be stemmer algorithm and replacement of the a/an applied to both step and template strings.
 *
 * The variables yield the matched strings from the step.
 *
 * @param step the String input.
 * @param stepTemplate the template to be matched to.
 * @param maxNumberOfOperations the maximum number of operations for the algorithm.
 *
 */
class RegMatching(
    val stepTemplate: StepTemplate,
    val step: String,
    val maxNumberOfOperations: Float
) {
    /**
     * cost of getting the best fitting pattern
     */
    val totalCost: Pair<Float, Float> by lazy {
        getCost()
    }

    /**
     * container of result of the matching
     */
    private val matchoutput: Pair<Map<String, String>, Float> by lazy { match() }
    /**
     * matched variables
     * if no match then emptyList
     */
    val variables: Map<String, String> by lazy {
        if (!isMisaligned()) getVars() else emptyMap()
    }

    /**
     * returns a map of variables to step values
     */
    private fun getVars(): Map<String, String> {
        return matchoutput.component1()
    }

    /**
     * if the template is misaligned (cost is above the maximum tolerance)
     */
    fun isMisaligned(): Boolean {
        return totalCost.component1() >= maxNumberOfOperations
    }

    /**
     * cost of matching
     */
    private fun getCost(): Pair<Float, Float> {
        return Pair(
            matchoutput.component2(),
            MatchingFunctions.considerVarLength(matchoutput.component1()) + matchoutput.component2()
        )
    }

    /**
     * executes the matching pipeline
     *
     * component1() the input strings are prepared
     * an apple -> a apple
     *
     * from the steptemplate the variable names are extracted into variablesMap
     * with null as value
     * [variable, ""]
     *
     * the steptemplate is transformed into a regex string
     * "making a {variable} outside." -> "making a (regex)outside."
     *
     * the regex string is used to match with the step
     * "making a (regex)outside." match to "making a castle outside"
     *
     * output is a list in the same order as the variables list.
     * these values usually contain an unwanted blank which is cut off in variablePostcalc()
     *
     * these values are added to the variablesMap and treated as output
     *
     * the operation number is the number of used operations on the string with exception of the
     * base algorithm described in this function
     *
     * @return Pair consisting of the matched variable value and the operations count
     */
    private fun match(): Pair<Map<String, String>, Float> {
        var operationNumber = 0.0f

        // copy the input strings to local variables
        val templatetext = MatchingFunctions.filterString(stepTemplate.toString(), false)
        val testText = MatchingFunctions.filterString(step, true)

        val (regstring, variablesList) = MatchingFunctions.templateStepToRegexString(
            templatetext
        )
        val reggedText = regstring.toRegex()

        // matching
        val (output, increase) = matchStrings(testText, reggedText, templatetext)
        operationNumber += increase
        val filteredMap: List<String> = output.map {
            MatchingFunctions.variablevaluePostcalc(
                it
            )
        }

        // mapping all outputs to the variables

        return if (filteredMap.size == variablesList.size) {
            val variablesMap = variablesList.zip(filteredMap).toMap()
            Pair(variablesMap, operationNumber)
        } else {
            operationNumber = maxNumberOfOperations
            Pair(emptyMap(), operationNumber)
        }
    }

    /**
     * matching function
     *
     * depending on whether the initial input suffices the matching with regex
     * the operation number can be increased
     *
     * if no match can be found the output is an emptyList with the maximum cost
     *
     * @param testText the text to be matched with
     * @param reggedText the regex string
     * @param templatetext only needed if a match does not provide any result
     *                      and is needed for a match in the next step
     *
     *@return List of variables matched to the string
     */
    private fun matchStrings(testText: String, reggedText: Regex, templatetext: String): Pair<List<String>, Float> {
        val matchedResult = reggedText.find(testText)
        var opIncrease = 0.0f

        var outputPair = Pair<List<String>, Float>(emptyList(), opIncrease)

        if (matchedResult == null && !reggedText.matches(testText)) {
            val (rematchResult, numOp) = rematch(testText, templatetext)
            outputPair = Pair(rematchResult, opIncrease + numOp)
        }
        if (matchedResult != null)
            outputPair = Pair(matchedResult.destructured.toList(), opIncrease)
        return outputPair
    }

    // Rematch with stemming, if already fit the template this should be not needed,
    // since that case cannot be excluded porter stemmer is needed!
    /**
     * Stemmer Algorithm postcalculation
     * reconstruct the Template with its variables
     * @param templateS the template string
     * @param variables the variables in their {} brackets and the position of them
     * @return rebuilt template string
     */
    private fun reconstructVars(templateS: String, variables: Map<Int, String>): String {
        val splittedTemplateString = templateS.split(" ")

        val reconStringout = splittedTemplateString.mapIndexed { index, string ->
            if (variables.containsKey(index))
                variables[index]
            else
                string
        }.joinToString(" ")

        return reconStringout
    }

    /**
     * Stemmer Algorithm preparation
     * preparation of the template stirng for stemming
     * @param templateS the template string ot be prepared
     * @return the variables and their position in the template string
     */
    private fun prepareTemplateString(templateS: String): Pair<String, Map<Int, String>> {
        val splittedTemplateString = templateS.split(" ")
        val variableLocations = mutableMapOf<Int, String>()

        val outstring = splittedTemplateString.mapIndexed { index, string ->
            if (MatchingFunctions.checkIfVar(string)) {
                variableLocations.put(index, string)
                "word"
            } else {
                string
            }
        }.joinToString(" ")
        return Pair(outstring, variableLocations)
    }

    /**
     * extracted method for readability
     * turns a text and its variables to a regex
     * @param stemmedsentence sentence from stemmer without variables
     * @param variables variables alongside their location in the string
     * @return a regex to start comparisons
     */
    private fun prepareTemplateToRegex(stemmedsentence: String, variables: Map<Int, String>): Regex {
        val vari = variables

        val preppedTemplate = reconstructVars(templateS = stemmedsentence, variables = vari)

        val (regexString) = MatchingFunctions.templateStepToRegexString(
            value = preppedTemplate
        )

        val regexText = regexString.toRegex()

        return regexText
    }

    /**
     * function to match again after stemming the words
     *
     * matching is according to the algorithm above but with stemmed forms
     *
     * @param testText the step
     * @param templatetext the step template
     * @return the list of matched strings to the variables and an increase in cost
     */
    private fun rematch(testText: String, templatetext: String): Pair<List<String>, Float> {
        var matchingcost = 0.0f

        // stepString
        val preppedString = StemmerHandler.stemWords(testText)
        val stepAsString = preppedString

        // template string
        val (sentence, variables) = prepareTemplateString(templateS = templatetext)
        val stemmedsentence = StemmerHandler.stemWords(sentence)

        // regex matching
        val regexText = prepareTemplateToRegex(stemmedsentence, variables)
        val matchresult = regexText.find(stepAsString)

        val matches = regexText.matches(stepAsString)

        matchingcost++

        // extend here if more algorithm have to be applied to strings or if
        // a rematch has to be made
        //

        return if (matchresult == null)
            if (matches)
                Pair(emptyList(), matchingcost)
            else
                Pair(emptyList(), maxNumberOfOperations)
        else Pair(matchresult.destructured.toList(), matchingcost)
    }
}
