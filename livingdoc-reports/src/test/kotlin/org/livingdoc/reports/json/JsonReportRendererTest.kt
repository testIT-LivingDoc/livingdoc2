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
import org.livingdoc.reports.json.JsonReportRenderer
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario

internal class JsonReportRendererTest {

    val cut = JsonReportRenderer()

    @Test
    fun `decisionTableResult is rendered correctly`() {
        val headerA = Header("a")
        val headerB = Header("b")
        val headerAPlusB = Header("a + b = ?")

        val row1 = Row(
            mapOf(
                headerA to Field("2"),
                headerB to Field("3"),
                headerAPlusB to Field("6")
            )
        )
        val row2 = Row(
            mapOf(
                headerA to Field("5"),
                headerB to Field("6"),
                headerAPlusB to Field("11")
            )
        )

        val decisionTable = DecisionTable(
            listOf(headerA, headerB, headerAPlusB),
            listOf(row1, row2)
        )

        val rowResult1 = RowResult.Builder().withRow(row1)
            .withFieldResult(
                headerA, FieldResult.Builder()
                    .withValue("2")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withFieldResult(
                headerB, FieldResult.Builder()
                    .withValue("3")
                    .withStatus(Status.Disabled("Disabled test"))
                    .build()
            )
            .withFieldResult(
                headerAPlusB, FieldResult.Builder()
                    .withValue("6")
                    .withStatus(Status.Failed(mockk(relaxed = true)))
                    .build()
            )
            .withStatus(Status.Executed)

        val rowResult2 = RowResult.Builder().withRow(row2)
            .withFieldResult(
                headerA, FieldResult.Builder()
                    .withValue("5")
                    .withStatus(Status.Skipped)
                    .build()
            )
            .withFieldResult(
                headerB, FieldResult.Builder()
                    .withValue("6")
                    .withStatus(Status.Manual)
                    .build()
            )
            .withFieldResult(
                headerAPlusB, FieldResult.Builder()
                    .withValue("11")
                    .withStatus(Status.Exception(mockk(relaxed = true)))
                    .build()
            )
            .withStatus(Status.Executed)

        val decisionTableResult = DecisionTableResult.Builder().withDecisionTable(decisionTable)

        decisionTableResult
            .withRow(rowResult1.build())
            .withRow(rowResult2.build())
            .withStatus(Status.Executed)

        val documentResult = DocumentResult.Builder()
            .withStatus(Status.Executed)
            .withResult(decisionTableResult.build())
            .build()

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
                                    "status": "manual"
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
        val stepResultC = StepResult.Builder().withValue("C").withStatus(Status.Manual).build()
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
                .withScenario(Scenario(listOf()))
                .withFixture(NoFixtureWrapper())
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
                            "C": "manual"
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
