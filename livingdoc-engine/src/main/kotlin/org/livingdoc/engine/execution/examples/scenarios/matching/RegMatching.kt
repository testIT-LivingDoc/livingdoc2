package org.livingdoc.engine.execution.examples.scenarios.matching

@Suppress("NestedBlockDepth")
internal class RegMatching(
    val stepTemplate: StepTemplate,
    val step: String,
    val maxCost: Int
) {
    val totalCost: Int by lazy {
        getCost()
    }
    val variables: Map<String, String> by lazy {
        if (!isMisaligned()) getVars() else emptyMap()
    }

    fun isMisaligned() = totalCost >= maxCost

    fun start() {

        match()
    }

    private lateinit var preparedtemplatetext: String
    private lateinit var reggedText: Regex

    private var testText = step
    private var cost = 0
    private var templatetext = stepTemplate.toString()
    private var templatetextTokenized: MutableMap<String, String> = mutableMapOf()

    private fun getCost(): Int {
        start()
        return cost
    }

    private fun getVars(): Map<String, String> {
        return templatetextTokenized
    }

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

    private fun matchStrings(): List<String> {

        val matchedResult = reggedText.find(testText)

        if (matchedResult == null) {
            println("COMPARING___________________________")
            println(stepTemplate.toString())
            println(step)
            println(cost)
            println("______________FAILED________________")
            cost++
            return emptyList()
        } else {
            val matched = matchedResult.destructured.toList()
            return matched
        }
    }

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
