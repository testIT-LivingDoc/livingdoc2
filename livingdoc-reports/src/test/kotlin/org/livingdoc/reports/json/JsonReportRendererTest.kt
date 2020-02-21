package org.livingdoc.reports.json

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.decisiontables.FieldResult
import org.livingdoc.results.examples.decisiontables.RowResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import org.livingdoc.results.examples.scenarios.StepResult

internal class JsonReportRendererTest {

    private val cut = JsonReportRenderer()

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
            listOf(row1, row2),
            TestDataDescription("Title", false, "descriptive text")
        )

        val rowResult1 = RowResult.Builder().withRow(row1)
            .withFieldResult(
                headerA,
                FieldResult.Builder()
                    .withValue("2")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withFieldResult(
                headerB,
                FieldResult.Builder()
                    .withValue("3")
                    .withStatus(Status.Disabled("Disabled test"))
                    .build()
            )
            .withFieldResult(
                headerAPlusB,
                FieldResult.Builder()
                    .withValue("6")
                    .withStatus(Status.Failed(mockk(relaxed = true)))
                    .build()
            )
            .withStatus(Status.Executed)

        val rowResult2 = RowResult.Builder().withRow(row2)
            .withFieldResult(
                headerA,
                FieldResult.Builder()
                    .withValue("5")
                    .withStatus(Status.Skipped)
                    .build()
            )
            .withFieldResult(
                headerB,
                FieldResult.Builder()
                    .withValue("6")
                    .withStatus(Status.Manual)
                    .build()
            )
            .withFieldResult(
                headerAPlusB,
                FieldResult.Builder()
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
            .withDocumentClass(JsonReportRendererTest::class.java)
            .withStatus(Status.Executed)
            .withResult(decisionTableResult.build())
            .withTags(emptyList())
            .build()

        val renderResult = cut.render(documentResult, false)

        assertThat(renderResult).hasLineCount(1)
        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                {
                    "results": [{
                        "title": "Title",
                        "description": ["descriptive text"],
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
                """.trimIndent()
        )

        val renderResultPretty = cut.render(documentResult, true)
        assertThat(renderResultPretty).isEqualToNormalizingNewlines(
            """
                {
                  "results": [{
                    "title": "Title",
                    "description": ["descriptive text"],
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
                """.trimIndent()
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

        val documentResult =
            DocumentResult.Builder().withDocumentClass(JsonReportRendererTest::class.java).withStatus(Status.Executed)
                .withResult(
                    ScenarioResult.Builder()
                        .withStep(stepResultA)
                        .withStep(stepResultB)
                        .withStep(stepResultC)
                        .withStep(stepResultD)
                        .withStep(stepResultE)
                        .withStep(stepResultF)
                        .withStatus(Status.Executed)
                        .withScenario(
                            Scenario(
                                listOf(
                                    Step("A"), Step("B"), Step("C"),
                                    Step("D"), Step("E"), Step("F")
                                ),
                                TestDataDescription(null, false, "")
                            )
                        )
                        .build()
                ).withTags(emptyList())
                .build()

        val renderResult = cut.render(documentResult, false)
        assertThat(renderResult).hasLineCount(1)
        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                {
                    "results": [{
                        "title": null,
                        "description": "",
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
                """.trimIndent()
        )
        val renderResultPretty = cut.render(documentResult, true)

        assertThat(renderResultPretty).isEqualToNormalizingNewlines(
            """
                {
                  "results": [{
                    "title": null,
                    "description": "",
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
                """.trimIndent()
        )
    }
}
