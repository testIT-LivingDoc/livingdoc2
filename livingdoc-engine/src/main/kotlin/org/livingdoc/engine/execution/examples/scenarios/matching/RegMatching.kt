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

    // variable to string matching container
    private var templatetextTokenized: MutableMap<String, String> = mutableMapOf()

    /**
     * matching method of the algorithm
     */
    private fun match() {

        tokenizetemplateText()
        reggedText = preparedtemplatetext.toRegex()
        val output = matchStrings()
        if (output.isEmpty())
        else {
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
     * TODO build the tolerance
     */
    private fun matchStrings(): List<String> {

        val matchedResult = reggedText.find(testText)

        if (matchedResult == null) {
            println("COMPARING___________________________")
            println(stepTemplate.toString())
            println(step)
            println(cost)
            println("______________FAILED________________")

            // TODO make some tolerance here for typos by using the levenshtein again
            // TODO scan word by word except for the variables
            cost++
            return emptyList()
        } else {
            val matched = matchedResult.destructured.toList()
            return matched
        }
    }

    /**
     * getting the variables and creating the regex by replacing the "{variable}"
     * with regexes
     */
    private fun tokenizetemplateText(): List<String> {
        val interntext = templatetext.split(" ")

        preparedtemplatetext = templatetext
        interntext.forEach { outer ->
            if (outer.contains("{") and outer.contains("}")) {
                var variable = outer

                val randomsymbols = variable.toCharArray()
                var beforeString = ""
                var afterString = ""
                var before = true
                var after = false
                randomsymbols.forEach {
                    if (it.equals('{'))
                        before = false

                    if (before)
                        beforeString += it

                    if (!before && after)
                        afterString += it
                    if (it.equals('}'))
                        after = true
                }

                variable = variable.replace(beforeString + "{", "")
                variable = variable.replace("}" + afterString, "")

                templatetextTokenized.put(variable, "")

                preparedtemplatetext = preparedtemplatetext.replace(outer, beforeString + "([\\w\\s]+)" + afterString)
            }
        }
        return interntext
    }
}
