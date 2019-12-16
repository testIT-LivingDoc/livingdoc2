package org.livingdoc.reports.html

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.repositories.model.decisiontable.Header

internal class HtmlReportRendererTest {

    private val cut = HtmlReportRenderer()

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
                                headerB to FieldResult("3", Status.Manual),
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
        val stepResultA = StepResult("A", Status.Executed)
        val stepResultB = StepResult("B", Status.Unknown)
        val stepResultC = StepResult("C", Status.Skipped)
        val stepResultD = StepResult("D", Status.Failed(mockk()))
        val stepResultE = StepResult("E", Status.Exception(mockk()))

        val documentResult = DocumentResult(
            Status.Executed,
            mutableListOf(
                ScenarioResult(
                    listOf(stepResultA, stepResultB, stepResultC, stepResultD, stepResultE),
                    Status.Executed
                )
            )
        )

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