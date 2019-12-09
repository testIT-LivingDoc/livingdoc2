package org.livingdoc.engine.reporting

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.NoFixtureWrapper
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.scenario.Scenario

internal class JsonReportRendererTest {

    val cut = JsonReportRenderer()

    @Test
    fun `decisionTableResult is rendered correctly`() {
        val headerA = Header("a")
        val headerB = Header("b")
        val headerAPlusB = Header("a + b = ?")

        val documentResult = DocumentResult(
            Status.Executed,
            mutableListOf(
                DecisionTableResult(
                    listOf(headerA, headerB, headerAPlusB),
                    listOf(
                        RowResult(
                            mapOf(
                                headerA to FieldResult("2", Status.Executed),
                                headerB to FieldResult("3", Status.Disabled("Disabled test")),
                                headerAPlusB to FieldResult("6", Status.Failed(mockk(relaxed = true)))
                            ), Status.Executed
                        ),
                        RowResult(
                            mapOf(
                                headerA to FieldResult("5", Status.Skipped),
                                headerB to FieldResult("6", Status.Unknown),
                                headerAPlusB to FieldResult("11", Status.Exception(mockk(relaxed = true)))
                            ), Status.Executed
                        )
                    ),
                    Status.Executed
                )
            )
        )

        val renderResult = cut.render(documentResult)

        assertThat(renderResult).isEqualToIgnoringWhitespace(
                """
                {
                    "results": [{
                        "rows": [{
                            "fields": {
                                "a": {
                                    "value": "2",
                                    "status": "executed"
                                },
                                "b": {
                                    "value": "3",
                                    "status": "disabled"
                                },
                                "a + b = ?": {
                                    "value": "6",
                                    "status": "failed"
                                }
                            },
                            "status": "executed"
                        }, {
                            "fields": {
                                "a": {
                                    "value": "5",
                                    "status": "skipped"
                                },
                                "b": {
                                    "value": "6",
                                    "status": "unknown"
                                },
                                "a + b = ?": {
                                    "value": "11",
                                    "status": "exception"
                                }
                            },
                            "status": "executed"
                        }],
                        "status": "executed"
                    }]
                }
                """
        )
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult.Builder().withValue("A").withStatus(Status.Executed).build()
        val stepResultB = StepResult.Builder().withValue("B")
                .withStatus(Status.Disabled("Disabled test"))
                .build()
        val stepResultC = StepResult.Builder().withValue("C").withStatus(Status.Unknown).build()
        val stepResultD = StepResult.Builder().withValue("D").withStatus(Status.Skipped).build()
        val stepResultE = StepResult.Builder().withValue("E").withStatus(Status.Failed(mockk())).build()
        val stepResultF = StepResult.Builder().withValue("F").withStatus(Status.Exception(mockk())).build()

        val documentResult = DocumentResult.Builder().withStatus(Status.Executed).withResult(
                ScenarioResult.Builder()
                        .withStep(stepResultA)
                        .withStep(stepResultB)
                        .withStep(stepResultC)
                        .withStep(stepResultD)
                        .withStep(stepResultE)
                        .withStep(stepResultF)
                        .withStatus(Status.Executed)
                        .ofScenario(Scenario(listOf()))
                        .ofFixture(NoFixtureWrapper())
                        .build()
        ).build()

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
                        "status": "executed"
                    }]
                }
                """
        )
    }
}
