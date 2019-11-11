package org.livingdoc.engine.execution.examples.decisiontables

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.mockkJClass
import org.livingdoc.engine.resources.DisabledDecisionTableDocument
import org.livingdoc.repositories.model.decisiontable.DecisionTable

internal class DecisionTableExecutionTest {

    @Test
    fun disabledDecisionTableExecute() {
        val decisionTableMock = mockkJClass(DecisionTable::class.java)
        val fixtureClass = DisabledDecisionTableDocument.DisabledDecisionTableFixture::class.java
        val cut = DecisionTableExecution(fixtureClass, decisionTableMock, null)

        val result = cut.execute().status

        Assertions.assertThat(result).isInstanceOf(Status.Disabled::class.java)
        Assertions.assertThat((result as Status.Disabled).reason).isEqualTo("Disabled DecisionTableFixture")
    }
}
