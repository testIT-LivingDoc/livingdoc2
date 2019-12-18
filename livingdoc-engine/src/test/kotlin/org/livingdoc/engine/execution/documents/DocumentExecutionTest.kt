package org.livingdoc.engine.execution.documents

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DocumentExecutionTest {
    @BeforeEach
    fun resetFixtures() {
        LifeCycleFixture.reset()
    }

    @Test
    fun `test life cycle of simple document`() {
        DocumentExecution(
            LifeCycleFixture::class.java,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true)
        ).execute()

        val fixture = LifeCycleFixture.callback
        verifySequence {
            fixture.before()
            fixture.after()
        }
    }
}
