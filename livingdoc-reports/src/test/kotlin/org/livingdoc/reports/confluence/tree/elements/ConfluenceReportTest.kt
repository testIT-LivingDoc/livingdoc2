package org.livingdoc.reports.confluence.tree.elements

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

internal class ConfluenceReportTest {
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
            .withStatus(Status.Failed(mockk(relaxed = true)))

        val documentResult = DocumentResult.Builder()
            .withDocumentClass(ConfluenceReportTest::class.java)
            .withStatus(Status.Executed)
            .withResult(decisionTableResult.build())
            .withTags(emptyList())
            .build()

        val renderResult = ConfluenceReport(documentResult).toString()

        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                <h2>Title</h2>
                <div>
                    <p>descriptive text</p>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>a</th>
                            <th>b</th>
                            <th>a + b = ?</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="highlight-green">2</td>
                            <td class="highlight-grey">3</td>
                            <td class="highlight-red">6
                                <ac:structured-macro ac:name="warning">
                                    <ac:rich-text-body>
                                        <pre></pre>
                                    </ac:rich-text-body>
                                </ac:structured-macro>
                            </td>
                        </tr>
                        <tr>
                            <td class="highlight-grey">5</td>
                            <td class="highlight-yellow">6</td>
                            <td class="highlight-red">11
                                <ac:structured-macro ac:name="warning">
                                    <ac:rich-text-body>
                                        <pre></pre>
                                    </ac:rich-text-body>
                                </ac:structured-macro>
                            </td>
                        </tr>
                        <tr>
                            <td class="highlight-green">2</td>
                            <td class="highlight-green">1</td>
                            <td class="highlight-blue">3</td>
                        </tr>
                    </tbody>
                </table>
            """
        )
    }

    @Test
    fun `scenarioResult is rendered correctly`() {
        val stepResultA = StepResult.Builder().withValue("A").withStatus(Status.Executed).build()
        val stepResultB = StepResult.Builder().withValue("B").withStatus(Status.Manual).build()
        val stepResultC = StepResult.Builder().withValue("C").withStatus(Status.Skipped).build()
        val stepResultD = StepResult.Builder().withValue("D").withStatus(Status.Failed(mockk())).build()
        val stepResultE = StepResult.Builder().withValue("E").withStatus(Status.Exception(mockk())).build()

        val documentResult = DocumentResult.Builder()
            .withDocumentClass(ConfluenceReportTest::class.java)
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

        val renderResult = ConfluenceReport(documentResult).toString()

        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                <h2>Title</h2>
                <div>
                    <p>descriptive text</p>
                </div>
                <ul>
                    <li style="color: rgb(0, 128, 0);">A</li>
                    <li style="color: rgb(255, 102, 0);">B</li>
                    <li style="color: rgb(165, 173, 186);">C</li>
                    <li style="color: rgb(255, 0, 0);">D</li>
                    <li style="color: rgb(255, 0, 0);">E</li>
                </ul>
            """
        )
    }

    @Test
    fun `tags are rendered correctly`() {
        val documentResult = DocumentResult.Builder()
            .withStatus(Status.Executed)
            .withDocumentClass(ConfluenceReportTest::class.java)
            .withTags(listOf("slow", "api"))
            .build()

        val renderResult = ConfluenceReport(documentResult).toString()

        assertThat(renderResult).isEqualToIgnoringWhitespace(
            """
                <h2>
                    <ac:structured-macro ac:name="status" ac:schema-version="1">
                        <ac:parameter ac:name="colour">Blue</ac:parameter>
                        <ac:parameter ac:name="title">slow</ac:parameter>
                    </ac:structured-macro>
                    <ac:structured-macro ac:name="status" ac:schema-version="1">
                        <ac:parameter ac:name="colour">Blue</ac:parameter>
                        <ac:parameter ac:name="title">api</ac:parameter>
                    </ac:structured-macro>
                </h2>
            """
        )
    }
}
