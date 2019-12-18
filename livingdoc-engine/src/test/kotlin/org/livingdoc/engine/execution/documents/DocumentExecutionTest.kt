package org.livingdoc.engine.execution.documents

import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData

class DocumentExecutionTest {
    @BeforeEach
    fun resetFixtures() {
        LifeCycleFixture.reset()
    }

    @Test
    fun `test life cycle of simple document`() {
        DocumentExecution(
            LifeCycleFixture::class.java,
            Document(listOf<TestData>()),
            DecisionTableToFixtureMatcher(),
            ScenarioToFixtureMatcher()
        ).execute()

        val fixture = LifeCycleFixture.callback
        verifySequence {
            fixture.before()
            fixture.after()
        }
    }
}
