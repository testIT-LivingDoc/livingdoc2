package org.livingdoc.engine.execution.examples.scenarios

import io.mockk.every
import io.mockk.verify
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.Result.*
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step

internal class ScenarioExecutorTest {

    val cut = ScenarioExecutor()

    @Test
    fun `executes a complete scenario`() {
        val step1 = Step("when the customer scans a banana for 49 cents")
        val step2 = Step("and an apple for 39 cents")
        val step3 = Step("when the customer checks out, the total sum is 88")
        val steps = listOf(step1, step2, step3)

        val scenarioResult = cut.execute(Scenario(steps), SelfCheckoutScenarioFixture::class.java, null)

        assertThat(scenarioResult.result).isEqualTo(Executed)
        assertThat(scenarioResult.steps).hasSize(3)
        scenarioResult.steps.forEach { step ->
            assertThat(step.result).isEqualTo(Executed)
        }
    }

    @Test
    fun `executes scenarios following the expected lifecycle`() {
        val steps = listOf(Step("step1"), Step("step2"))

        val resultScenario = cut.execute(Scenario(steps), LifeCycleFixture::class.java, null)
        assertThat(resultScenario.result).isEqualTo(Executed)

        val fixture = LifeCycleFixture.callback

        verifySequence {
            fixture.before()
            fixture.step1()
            fixture.step2()
            fixture.after()
        }
    }

    @Test
    fun `multiple steps matching different templates can be mapped to the same method`() {
        val steps = listOf(
            Step("step2"),
            Step("Alternate template for step2")
        )

        cut.execute(Scenario(steps), LifeCycleFixture::class.java, null)

        val fixture = LifeCycleFixture.callback
        verify(exactly = 2) { fixture.step2() }
    }

    @Nested
    inner class `when executing steps with parameters` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        @Test
        fun `parameter values are passed as method parameters with the same name`() {
            // requires compilation with "-parameters" (Java8)
            // Reminder: To configure this in Intellij IDEA, go to "Settings" > "Build, Execution, Deployment" >
            //           "Compiler" > "Java Compiler" and add "-parameters" to "Additional command line parameters",
            //           then clean and rebuild the project.
            execute(Step("Step with parameter: wonderful"))

            verify { fixture.parameterizedStep("wonderful") }
        }

        @Test
        fun `parameter values are passed to methods based on explicit name bindings`() {
            execute(Step("Step with parameter passed by explicit name bindings: explicit"))

            verify { fixture.parameterizedStepWithBinding("explicit") }
        }

        @Test
        fun `a mismatching parameter results in "Exception"`() {
            val result = execute(Step("Step with mismatching parameter: Oh noes!")).result

            assertThat(result).isInstanceOf(Exception::class.java)
        }
    }

    @Nested
    inner class `when an assertion fails during execution of a scenario step` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        @Test
        fun `the result of the scenario is Executed`() {
            every { fixture.step1() } throws AssertionError()

            val result = execute(Step("step1"), Step("step2")).result

            assertThat(result).isInstanceOf(Executed::class.java)
        }

        @Test
        fun `the result of the step is Failed`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult = execute(Step("step1"), Step("step2")).steps[0].result

            assertThat(stepResult).isInstanceOf(Failed::class.java)
        }

        @Test
        fun `the following steps are Skipped`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult = execute(Step("step1"), Step("step2")).steps[1].result

            assertThat(stepResult).isInstanceOf(Skipped::class.java)
        }

        @Test
        fun `the teardown commands are executed`() {
            every { fixture.step1() } throws AssertionError()

            execute()

            verify {
                fixture.after1()
                fixture.after2()
            }
        }
    }

    @Nested
    inner class `when an exception is thrown` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        @Nested
        inner class duringasetupcommandBefore {

            @Test
            fun `the result is Exception`() {
                every { fixture.before1() } throws IllegalStateException()

                val result = execute(Step("step1"), Step("step2")).result

                assertThat(result).isInstanceOf(Exception::class.java)
            }

            @Test
            fun `the following setup commands are not invoked`() {
                every { fixture.before1() } throws IllegalStateException()

                execute(Step("step1"), Step("step2"))

                verify(exactly = 0) { fixture.before2() }
            }

            @Test
            fun `no scenario steps are executed`() {
                every { fixture.before1() } throws IllegalStateException()

                execute(Step("step1"), Step("step2"))

                verify(exactly = 0) {
                    fixture.step1()
                    fixture.step2()
                }
            }

            @Test
            fun `the results of all steps are Skipped`() {
                every { fixture.before1() } throws IllegalStateException()

                val result = execute(Step("step1"), Step("step2"))

                assertThat(result.steps[0].result).isEqualTo(Skipped)
                assertThat(result.steps[1].result).isEqualTo(Skipped)
            }

            @Test
            fun `teardown commands, however, are invoked`() {
                every { fixture.before1() } throws IllegalStateException()

                execute(Step("step1"), Step("step2"))

                verify {
                    fixture.after1()
                    fixture.after2()
                }
            }
        }

        @Nested
        inner class `during execution of a step` {

            @Test
            fun `the result of the scenario is Executed`() {
                every { fixture.step1() } throws IllegalStateException()

                val result = execute(Step("step1"), Step("step2")).result

                assertThat(result).isInstanceOf(Executed::class.java)
            }

            @Test
            fun `the result of the step is Exception`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult = execute(Step("step1"), Step("step2")).steps[0].result

                assertThat(stepResult).isInstanceOf(Exception::class.java)
            }

            @Test
            fun `the following steps are Skipped`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult = execute(Step("step1"), Step("step2")).steps[1].result

                assertThat(stepResult).isInstanceOf(Skipped::class.java)
            }

            @Test
            fun `the teardown commands are executed`() {
                every { fixture.step1() } throws IllegalStateException()

                execute()

                verify {
                    fixture.after1()
                    fixture.after2()
                }
            }
        }

        @Nested
        inner class duringateardowncommandAfter {

            @Test
            fun `the result of the scenario is Exception`() {
                every { fixture.after1() } throws IllegalStateException()

                val result = execute().result

                assertThat(result).isInstanceOf(Exception::class.java)
            }

            @Test
            fun `all exceptions are collected and attached to the Exception result`() {
                val exception1 = IllegalStateException()
                val exception2 = IllegalStateException()
                every { fixture.after1() } throws exception1
                every { fixture.after2() } throws exception2

                val result = execute().result as Result.Exception

                assertThat(result.exception).isInstanceOf(ScenarioExecution.AfterMethodExecutionException::class.java)
                assertThat(result.exception.suppressed).containsExactlyInAnyOrder(exception1, exception2)
            }

            @Test
            fun `subsequent teardown commands are executed`() {
                every { fixture.after1() } throws IllegalStateException()

                execute()

                verify { fixture.after2() }
            }
        }
    }

    @BeforeEach
    fun reset() {
        LifeCycleFixture.reset()
        ExtendedLifeCycleFixture.reset()
    }

    private fun execute(vararg steps: Step): ScenarioResult {
        val scenario = Scenario(steps.asList())
        return cut.execute(scenario, ExtendedLifeCycleFixture::class.java, null)
    }
}
