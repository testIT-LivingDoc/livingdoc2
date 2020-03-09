package org.livingdoc.jvm.scenario

import io.mockk.clearMocks
import io.mockk.mockkClass
import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.Step

internal class EmptyFixture

internal class CalculatorFixture {
    @org.livingdoc.api.fixtures.scenarios.Step("a calculator")
    fun setup() {
    }

    @org.livingdoc.api.fixtures.scenarios.Step("I add 2 and 3")
    fun add() {
    }

    @org.livingdoc.api.fixtures.scenarios.Step("I get 5")
    fun verify() {
    }
}

class ExtendedLifeCycleFixture {
    @Before
    fun before1() {
        callback.before1()
    }

    @Before
    fun before2() {
        callback.before2()
    }

    @Step("step1")
    fun step1() {
        callback.step1()
    }

    @Step("step2")
    fun step2() {
        callback.step2()
    }

    @Step("Step with parameter: {parameterName}")
    fun parameterizedStep(parameterName: String?) {
        callback.parameterizedStep(parameterName)
    }

    @Step("Step with parameter passed by explicit name bindings: {bound}")
    fun parameterizedStepWithBinding(@Binding("bound") param: String?) {
        callback.parameterizedStepWithBinding(param)
    }

    @Step("Step with mismatching parameter: {mismatch}")
    fun stepWithMissingParameter(unused: String) {
        callback.stepWithMissingParameter(unused)
    }

    @After
    fun after1() {
        callback.after1()
    }

    @After
    fun after2() {
        callback.after2()
    }

    interface Callback {
        fun before1()
        fun before2()
        fun step1()
        fun step2()
        fun parameterizedStep(parameterName: String?)
        fun parameterizedStepWithBinding(param: String?)
        fun stepWithMissingParameter(unused: String)
        fun after1()
        fun after2()
    }

    companion object {
        var callback: Callback = mockkClass(Callback::class)
        fun reset() {
            clearMocks(callback)
        }
    }
}

class LifeCycleFixture {

    companion object {
        var callback = mockkClass(CallbackInterface::class)
        fun reset() {
            clearMocks(callback)
        }
    }

    interface CallbackInterface {
        fun before()
        fun step1()
        fun step2()
        fun step3()
        fun after()
    }

    @Before
    fun before() {
        callback.before()
    }

    @org.livingdoc.api.fixtures.scenarios.Step("step1")
    fun step1() {
        callback.step1()
    }

    @org.livingdoc.api.fixtures.scenarios.Step("step2")
    fun step2() {

        callback.step2()
    }

    @org.livingdoc.api.fixtures.scenarios.Step("step3")
    fun step3() {

        callback.step3()
    }

    @After
    fun after() {

        callback.after()
    }
}

internal class FullScenario {

    @org.livingdoc.api.fixtures.scenarios.Step("when the customer scans a {fruit} for 49 cents")
    fun `when the customer scans a banana for 49 cents`() {
    }

    @org.livingdoc.api.fixtures.scenarios.Step("and a {fruit} for 39 cents")
    fun `and an apple for 39 cents`() {
    }

    @org.livingdoc.api.fixtures.scenarios.Step("when the customer checks out, the total sum is 88")
    fun `when the customer checks out, the total sum is 88`() {
    }
}

/*

    @Test
    fun `executes scenarios following the expected lifecycle`() {
        val steps = listOf(Step("step1"), Step("step2"))

        val resultScenario = executeScenario(Scenario(steps), LifeCycleFixture::class.java)
        Assertions.assertThat(resultScenario.status).isEqualTo(Status.Executed)

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

        executeScenario(Scenario(steps), LifeCycleFixture::class.java)

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
            val result = execute(Step("Step with mismatching parameter: Oh noes!")).status

            Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
        }
    }

    @Nested
    inner class `when an assertion fails during execution of a scenario step` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        @Test
        fun `the result of the scenario is Executed`() {
            every { fixture.step1() } throws AssertionError()

            val result = execute(Step("step1"), Step("step2")).status

            Assertions.assertThat(result).isInstanceOf(Status.Executed::class.java)
        }

        @Test
        fun `the result of the step is Failed`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult = execute(Step("step1"), Step("step2")).steps[0].status

            Assertions.assertThat(stepResult).isInstanceOf(Status.Failed::class.java)
        }

        @Test
        fun `the following steps are Skipped`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult = execute(Step("step1"), Step("step2")).steps[1].status

            Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
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

                val result = execute(Step("step1"), Step("step2")).status

                Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
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

                Assertions.assertThat(result.steps[0].status).isEqualTo(Status.Skipped)
                Assertions.assertThat(result.steps[1].status).isEqualTo(Status.Skipped)
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

                val result = execute(Step("step1"), Step("step2")).status

                Assertions.assertThat(result).isInstanceOf(Status.Executed::class.java)
            }

            @Test
            fun `the result of the step is Exception`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult = execute(Step("step1"), Step("step2")).steps[0].status

                Assertions.assertThat(stepResult).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `the following steps are Skipped`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult = execute(Step("step1"), Step("step2")).steps[1].status

                Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
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

                val result = execute().status

                Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `all exceptions are collected and attached to the Exception result`() {
                val exception1 = IllegalStateException()
                val exception2 = IllegalStateException()
                every { fixture.after1() } throws exception1
                every { fixture.after2() } throws exception2

                val result = execute().status as Status.Exception

                Assertions.assertThat(result.exception)
                    .isInstanceOf(ScenarioFixtureWrapper.AfterMethodExecutionException::class.java)
                Assertions.assertThat(result.exception.suppressed).containsExactlyInAnyOrder(exception1, exception2)
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
        return executeScenario(scenario, ExtendedLifeCycleFixture::class.java)
    }

    private fun executeScenario(scenario: Scenario, fixtureClass: Class<*>): ScenarioResult {
        return ScenarioFixtureWrapper(fixtureClass).execute(scenario)
    }

}
*/
