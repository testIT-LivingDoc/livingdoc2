package org.livingdoc.engine.execution.groups

import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GroupExecutionTest {
    @BeforeEach
    fun resetFixtures() {
        LifeCycleFixture.reset()
    }

    @Test
    fun `test life cycle of simple group`() {
        GroupExecution(
            LifeCycleFixture::class.java,
            listOf(),
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
