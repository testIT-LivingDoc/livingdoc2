package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.converters.JSONConverter
import org.livingdoc.repositories.format.DataTable

@ExecutableDocument("local://MultilineArgumentCalculator.feature")
class MultilineArgumentCalculatorGherkin {
    @ScenarioFixture
    class CalculatorScenario {
        private var lastResult: Float? = null
        private lateinit var cut: Calculator

        @Step("a calculator")
        fun `initialize calculator`() {
            cut = Calculator()
        }

        @Step("I perform: {calculations}")
        fun `calculate data table`(@Converter(JSONConverter::class) @Binding("calculations") table: DataTable) {
            table.rows.forEach { assertThat(cut.sum(it[0].toFloat(), it[1].toFloat())).isEqualTo(it[2].toFloat()) }
        }

        @Step("I read {text}")
        fun `ignore text`(@Binding("text") text: String) {
        }

        @Step("I do nothing")
        fun `do nothing`() {
        }
    }
}
