package org.livingdoc.reports.html

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
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario

internal class HtmlReportRendererTest {

    private val cut = HtmlReportRenderer()

    @Test
    fun `decisionTableResult is rendered correctly`() {
        val headerA = Header("a")
        val headerB = Header("b")
        val headerAPlusB = Header("a + b = ?")

        val row1 = mapOf(
            headerA to Field("2"),
            headerB to Field("3"),
            headerAPlusB to Field("6")
        )
        val row2 = mapOf(
            headerA to Field("5"),
            headerB to Field("6"),
            headerAPlusB to Field("11")
        )

        val decisionTable = DecisionTable(
            listOf(headerA, headerB, headerAPlusB),
            listOf(Row(row1), Row(row2))
        )

        val rowResult1 = RowResult.Builder().withDecisionTable(decisionTable)
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

        val rowResult2 = RowResult.Builder().withDecisionTable(decisionTable)
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
                <!DOCTYPE html>
                <html>
                    <head>
                        ${HtmlReportTemplate.HTML_HEAD_STYLE_CONTENT}
                    </head>
                    <body>
                        <table>
                            <tr>
                               <th class="border-black-onepx">a</th>
                               <th class="border-black-onepx">b</th>
                               <th class="border-black-onepx">a + b = ?</th>
                            </tr>
                            <tr>
                                <td class="border-black-onepx background-executed"><span class="result-value">2</span></td>
                                <td class="border-black-onepx background-manual"><span class="result-value">3</span></td>
                                <td class="border-black-onepx background-failed"><span class="result-value">6</span><a href="#popup1" class="icon-failed"></a></td>
                             </tr>
                             <tr>
                                <td class="border-black-onepx background-skipped"><span class="result-value">5</span></td>
                                <td class="border-black-onepx background-unknown"><span class="result-value">6</span></td>
                                <td class="border-black-onepx background-exception"><span class="result-value">11</span><a href="#popup2" class="icon-exception"></a></td>
                             </tr>
                        </table>

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
        )
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult.Builder().withValue("A").withStatus(Status.Executed).build()
        val stepResultB = StepResult.Builder().withValue("B").withStatus(Status.Unknown).build()
        val stepResultC = StepResult.Builder().withValue("C").withStatus(Status.Skipped).build()
        val stepResultD = StepResult.Builder().withValue("D").withStatus(Status.Failed(mockk())).build()
        val stepResultE = StepResult.Builder().withValue("E").withStatus(Status.Exception(mockk())).build()

        val documentResult = DocumentResult.Builder().withStatus(Status.Executed).withResult(
            ScenarioResult.Builder()
                .withStep(stepResultA)
                .withStep(stepResultB)
                .withStep(stepResultC)
                .withStep(stepResultD)
                .withStep(stepResultE)
                .withStatus(Status.Executed)
                .withScenario(Scenario(listOf()))
                .withFixture(NoFixtureWrapper())
                .build()
        ).build()

        val renderResult = cut.render(documentResult)

        assertThat(renderResult).isEqualToIgnoringWhitespace(
                """
                <!DOCTYPE html>
                <html>
                    <head>
                       ${HtmlReportTemplate.HTML_HEAD_STYLE_CONTENT}
                    </head>
                    <body>
                        <ul>
                            <li class="background-executed">A</li>
                            <li class="background-unknown">B</li>
                            <li class="background-skipped">C</li>
                            <li class="background-failed">D</li>
                            <li class="background-exception">E</li>
                        </ul>
                    </body>
                </html>
                """
        )
    }
}
