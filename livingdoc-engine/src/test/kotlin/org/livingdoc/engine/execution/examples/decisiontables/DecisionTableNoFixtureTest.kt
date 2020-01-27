package org.livingdoc.engine.execution.examples.decisiontables

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status

internal class DecisionTableNoFixtureTest {

    @Test
    fun manualDecisionTableExecute() {
        val input = Header("input")
        val check = Header("check")
        val headers = arrayListOf(input, check)

        val row1 = Row(mapOf(input to Field("r1i"), check to Field("r1c")))
        val row2 = Row(mapOf(input to Field("r2i"), check to Field("r2c")))
        val rows = arrayListOf(row1, row2)

        val decisionTableMock = DecisionTable(
            headers,
            rows,
            TestDataDescription("MANUAL Test1", true, "")
        )

        val cut = DecisionTableNoFixture()
        val result = cut.execute(decisionTableMock).status

        Assertions.assertThat(result).isInstanceOf(Status.Manual::class.java)
    }
}
