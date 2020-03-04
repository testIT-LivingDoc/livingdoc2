package org.livingdoc.reports.html

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

internal class HtmlReportRendererTest {

    private val cut = HtmlReportRenderer()

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
        val row3 = Row(
            mapOf(
                headerA to Field("2"),
                headerB to Field("1"),
                headerAPlusB to Field("")
            )
        )

        val decisionTable = DecisionTable(
            listOf(headerA, headerB, headerAPlusB),
            listOf(row1, row2, row3),
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

        val rowResult3 = RowResult.Builder().withRow(row3)
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
                    .withValue("1")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withFieldResult(
                headerAPlusB,
                FieldResult.Builder()
                    .withValue("")
                    .withStatus(Status.ReportActualResult("3"))
                    .build()
            )
            .withStatus(Status.Executed)

        val decisionTableResult = DecisionTableResult.Builder().withDecisionTable(decisionTable)

        decisionTableResult
            .withRow(rowResult1.build())
            .withRow(rowResult2.build())
            .withRow(rowResult3.build())
            .withStatus(Status.Executed)

        val documentResult = DocumentResult.Builder()
            .withDocumentClass(HtmlReportRendererTest::class.java)
            .withStatus(Status.Executed)
            .withResult(decisionTableResult.build())
            .withTags(emptyList())
            .build()

        val renderResult = cut.render(documentResult)

        assertThat(renderResult.trimIndent()).isEqualToIgnoringWhitespace(
            """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta charset="UTF-8">
                        ${HtmlReportTemplate.HTML_HEAD_STYLE_CONTENT}
                        ${HtmlReportTemplate.HTML_HEAD_SCRIPT_CONTENT}
                    </head>
                    <body>
                        <div class="flex flex-row">
                            <div class="column">
                                <h2>HtmlReportRendererTest (${"%.3f".format(0 / 1000f)}s)</h2>
                                <div>
                                 <p>tags: </p>
                                </div>
                                <h2>Title</h2>
                                <div>
                                    <p>descriptive text</p>
                                </div>
                                <table>
                                    <thead>
                                        <tr>
                                           <th class="border-black-onepx">a</th>
                                           <th class="border-black-onepx">b</th>
                                           <th class="border-black-onepx">a + b = ?</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td class="border-black-onepx background-executed"><span class="result-value">2</span></td>
                                            <td class="border-black-onepx background-disabled"><span class="result-value">3</span></td>
                                            <td class="border-black-onepx background-failed"><span class="result-value">6</span><a href="#popup1" class="icon-failed"></a></td>
                                         </tr>
                                         <tr>
                                            <td class="border-black-onepx background-skipped"><span class="result-value">5</span></td>
                                            <td class="border-black-onepx background-manual"><span class="result-value">6</span></td>
                                            <td class="border-black-onepx background-exception"><span class="result-value">11</span><a href="#popup2" class="icon-exception"></a></td>
                                         </tr>
                                         <tr>
                                            <td class="border-black-onepx background-executed"><span class="result-value">2</span></td>
                                            <td class="border-black-onepx background-executed"><span class="result-value">1</span></td>
                                            <td class="border-black-onepx background-report-result"><span class="result-value">3</span></td>
                                         </tr>
                                     </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="footer">
                            <p><a href="index.html">↩ Index</a></p>
                            <p>Generated with <strong>Living Doc 2</strong>.</p>
                        </div>

                        <div id="popup1" class="overlay">
                            <div class="popup">
                                <h2></h2>
                                <a class="close" href="#">&times;</a>
                                <div class="content">
                                    <pre>
                                    </pre>
                                </div>
                            </div>
                        </div>

                        <div id="popup2" class="overlay">
                            <div class="popup">
                                <h2></h2>
                                <a class="close" href="#">&times;</a>
                                <div class="content">
                                    <pre>
                                    </pre>
                                </div>
                            </div>
                        </div>
                    </body>
                </html>
                """
                .trimIndent().replace("\\s+", " "))
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult.Builder().withValue("A").withStatus(Status.Executed).build()
        val stepResultB = StepResult.Builder().withValue("B").withStatus(Status.Manual).build()
        val stepResultC = StepResult.Builder().withValue("C").withStatus(Status.Skipped).build()
        val stepResultD = StepResult.Builder().withValue("D").withStatus(Status.Failed(mockk())).build()
        val stepResultE = StepResult.Builder().withValue("E").withStatus(Status.Exception(mockk())).build()

        val documentResult = DocumentResult.Builder()
            .withDocumentClass(HtmlReportRendererTest::class.java)
            .withStatus(Status.Executed)
            .withResult(
                ScenarioResult.Builder()
                    .withStep(stepResultA)
                    .withStep(stepResultB)
                    .withStep(stepResultC)
                    .withStep(stepResultD)
                    .withStep(stepResultE)
                    .withStatus(Status.Executed)
                    .withScenario(
                        Scenario(
                            listOf(
                                Step("A"), Step("B"), Step("C"),
                                Step("D"), Step("E")
                            ),
                            TestDataDescription("Title", false, "descriptive text")
                        )
                    )
                    .build()
            ).withTags(emptyList())
            .build()

        val renderResult = cut.render(documentResult)

        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta charset="UTF-8">
                        ${HtmlReportTemplate.HTML_HEAD_STYLE_CONTENT}
                        ${HtmlReportTemplate.HTML_HEAD_SCRIPT_CONTENT}
                    </head>
                    <body>
                        <div class="flex flex-row">
                            <div class="column">
                                <h2>HtmlReportRendererTest (${"%.3f".format(0 / 1000f)}s)</h2>
                                <div>
                                 <p>tags: </p>
                                </div>
                                <h2>Title</h2>
                                <div>
                                    <p>descriptive text</p>
                                </div>
                                <ul>
                                    <li class="background-executed">A</li>
                                    <li class="background-manual">B</li>
                                    <li class="background-skipped">C</li>
                                    <li class="background-failed">D</li>
                                    <li class="background-exception">E</li>
                                </ul>
                            </div>
                        </div>
                        <div class="footer">
                            <p><a href="index.html">↩ Index</a></p>
                            <p>Generated with <strong>Living Doc 2</strong>.</p>
                        </div>
                    </body>
                </html>
                """
        )
    }
}
