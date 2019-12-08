package org.livingdoc.engine.execution.examples.scenarios.matching

import org.livingdoc.engine.algo.LevenshteinAlgo

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
            val mr = rematch()
            if (!mr.isEmpty()) {
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
     * method extracted out of rematch
     *
     * @return strings to be changed
     */
    private fun foreachMap(markermap: Map<String, String>): Map<String, String> {
        val regexTransformMap = mutableMapOf<String, String>()
        markermap.forEach {
            if (!it.key.equals(it.value))
                regexTransformMap.put(it.key, it.value)
        }
        return regexTransformMap
    }

    /**
     * if the first try did not succeed then we try replacing more values with regex
     *and perform the regex matching on the adapted template string
     *
     * @return the matching list of Strings
     */
    private fun rematch(): List<String> {
        var splittedTemplate = templatetext.toString().split(" ").toMutableList()
        var splittedStep = step.toString().split(" ")

        var values = extractedWhile(splittedStep, splittedTemplate)

        var costincrease = values.second
        var markermap = values.first

        var regexTransformMap = foreachMap(markermap)

        var stringId = 0

        splittedTemplate.forEach {
            if (regexTransformMap.contains(it)) {

                splittedTemplate[stringId] = regexTransformMap[it].toString()
            }
            stringId++
        }

        var spltteredString = ""
        spltteredString += splittedTemplate.first()
        splittedTemplate.forEach {
            if (!it.equals(splittedTemplate.first()))
                spltteredString += " " + it
        }
        cost += costincrease
        val innerreggedText = tokenizetemplateText(ininterntext = spltteredString)

        var regtext = innerreggedText.first()
        innerreggedText.forEach {
            if (!it.equals(innerreggedText.first()))
                regtext += " " + it
        }
        val regularized = regtext.toRegex()
        val rt = regularized.find(testText)
        if (rt != null) {
            return rt.destructured.toList()
        } else {
            return emptyList()
        }
    }

    /**
     * extracted method out of rematch,
     * iterates over both strings and finds matching strings and gives an evaluation via leveshtein
     *
     * @param splittedStep step split up to List of strings
     * @param splittedTemplate template split up to strings
     * @return Map of changed strings and the cost of this change
     */
    private fun extractedWhile(
        splittedStep: List<String>,
        splittedTemplate: List<String>
    ): Pair<Map<String, String>, Int> {
        var pointer1 = 0
        var pointer2 = 0
        var costincrease = 0

        val markermap = mutableMapOf<String, String>()

        while (pointer2 < splittedStep.size - 1 && pointer1 < splittedTemplate.size - 1) {

            if (checkIfVar(splittedTemplate[pointer1])) {

                pointer1++
            } else {
                if (splittedStep[pointer2].equals(splittedTemplate[pointer1])) {
                    markermap.put(splittedStep[pointer2], splittedStep[pointer2])
                    pointer1++
                    pointer2++
                } else {
                    val innerdistance = 2
                    var dist = LevenshteinAlgo.levenshtein(splittedTemplate[pointer1], splittedStep[pointer2])
                    if (dist < innerdistance && costincrease + dist < maxCost) {
                        markermap.put(splittedTemplate[pointer1], splittedStep[pointer2])
                        costincrease += dist
                        pointer1++
                        pointer2++
                    } else if (dist >= innerdistance) {
                        pointer2++
                    }
                }
            }
        }
        return Pair(markermap, costincrease)
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
