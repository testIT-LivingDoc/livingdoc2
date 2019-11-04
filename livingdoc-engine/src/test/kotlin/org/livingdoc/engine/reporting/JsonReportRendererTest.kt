package org.livingdoc.engine.reporting

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.repositories.model.decisiontable.Header

internal class JsonReportRendererTest {

    val cut = JsonReportRenderer()

    @Test
    fun `decisionTableResult is rendered correctly`() {
        val headerA = Header("a")
        val headerB = Header("b")
        val headerAPlusB = Header("a + b = ?")

        val documentResult = DocumentResult(
                Result.Executed,
                mutableListOf(DecisionTableResult(
                        listOf(headerA, headerB, headerAPlusB),
                        listOf(
                                RowResult(mapOf(
                                        headerA to FieldResult("2", Result.Executed),
                                        headerB to FieldResult("3", Result.Disabled("Disabled test")),
                                        headerAPlusB to FieldResult("6", Result.Failed(mockk(relaxed = true)))
                                ), Result.Executed),
                                RowResult(mapOf(
                                        headerA to FieldResult("5", Result.Skipped),
                                        headerB to FieldResult("6", Result.Unknown),
                                        headerAPlusB to FieldResult("11", Result.Exception(mockk(relaxed = true)))
                                ), Result.Executed)
                        ),
                        Result.Executed
                )))

        val renderResult = cut.render(documentResult)

        assertThat(renderResult).isEqualToIgnoringWhitespace(
                """
                {
                    "results": [{
                        "rows": [{
                            "fields": {
                                "a": {
                                    "value": "2",
                                    "result": "executed"
                                },
                                "b": {
                                    "value": "3",
                                    "result": "disabled"
                                },
                                "a + b = ?": {
                                    "value": "6",
                                    "result": "failed"
                                }
                            },
                            "result": "executed"
                        }, {
                            "fields": {
                                "a": {
                                    "value": "5",
                                    "result": "skipped"
                                },
                                "b": {
                                    "value": "6",
                                    "result": "unknown"
                                },
                                "a + b = ?": {
                                    "value": "11",
                                    "result": "exception"
                                }
                            },
                            "result": "executed"
                        }],
                        "result": "executed"
                    }]
                }
                """)
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult("A", Result.Executed)
        val stepResultB = StepResult("B", Result.Disabled("Disabled test"))
        val stepResultC = StepResult("C", Result.Unknown)
        val stepResultD = StepResult("D", Result.Skipped)
        val stepResultE = StepResult("E", Result.Failed(mockk()))
        val stepResultF = StepResult("F", Result.Exception(mockk()))

        val documentResult = DocumentResult(
                Result.Executed,
                mutableListOf(ScenarioResult(
                        listOf(stepResultA, stepResultB, stepResultC, stepResultD, stepResultE, stepResultF),
                        Result.Executed
                )))

        val renderResult = cut.render(documentResult)

        assertThat(renderResult).isEqualToIgnoringWhitespace(
                """
                {
                    "results": [{
                        "steps": [{
                            "A": "executed"
                        }, {
                            "B": "disabled"
                        }, {
                            "C": "unknown"
                        }, {
                            "D": "skipped"
                        }, {
                            "E": "failed"
                        }, {
                            "F": "exception"
                        }],
                        "result": "executed"
                    }]
                }
                """)
    }
}
