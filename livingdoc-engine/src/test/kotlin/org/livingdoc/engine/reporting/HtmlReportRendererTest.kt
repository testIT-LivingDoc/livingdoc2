package org.livingdoc.engine.reporting

import com.nhaarman.mockitokotlin2.mock
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


internal class HtmlReportRendererTest {

    val cut = HtmlReportRenderer()

    @Test
    fun `decisionTableResult is rendered correctly`() {
        val headerA = Header("a")
        val headerB = Header("b")
        val headerAPlusB = Header("a + b = ?")

        val documentResult = DocumentResult(
                mutableListOf(DecisionTableResult(
                        listOf(headerA, headerB, headerAPlusB),
                        listOf(
                                RowResult(mapOf(
                                        headerA to FieldResult("2", Result.Executed),
                                        headerB to FieldResult("3", Result.Executed),
                                        headerAPlusB to FieldResult("6", Result.Failed(mock()))
                                ), Result.Executed),
                                RowResult(mapOf(
                                        headerA to FieldResult("5", Result.Skipped),
                                        headerB to FieldResult("6", Result.Unknown),
                                        headerAPlusB to FieldResult("11", Result.Exception(mock()))
                                ), Result.Executed)
                        ),
                        Result.Executed
                )))

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
                                <td class="border-black-onepx background-executed"><span class="result-value">3</span></td>
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
                """)
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult("A", Result.Executed)
        val stepResultB = StepResult("B", Result.Unknown)
        val stepResultC = StepResult("C", Result.Skipped)
        val stepResultD = StepResult("D", Result.Failed(mock()))
        val stepResultE = StepResult("E", Result.Exception(mock()))

        val documentResult = DocumentResult(
                mutableListOf(ScenarioResult(
                        listOf(stepResultA, stepResultB, stepResultC, stepResultD, stepResultE),
                        Result.Executed
                )))

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
                """)
    }
}
