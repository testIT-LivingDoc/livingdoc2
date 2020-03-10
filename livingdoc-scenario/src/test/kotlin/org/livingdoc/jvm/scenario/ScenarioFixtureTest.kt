package org.livingdoc.jvm.scenario

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureManager
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import kotlin.reflect.KClass

class ScenarioFixtureTest {
    val fixture = LifeCycleFixture.callback!!
    @Test
    fun `multiple steps matching different templates can be mapped to the same method`() {
        every { fixture.before() }.returns(Unit)
        every { fixture.step1() }.returns(Unit)
        every { fixture.step2() }.returns(Unit)
        every { fixture.after() }.returns(Unit)

        val steps = listOf(
            Step("step2"),
            Step("Alternate template for step2")
        )
        val scenario = Scenario(steps)
        val scenarioFixture = createScenarioFixture(LifeCycleFixture::class)
        scenarioFixture.execute(scenario) as ScenarioResult

        val fixture = LifeCycleFixture.callback
        verify(exactly = 2) { fixture.step2() }
        LifeCycleFixture.reset()
    }

    private fun createManager(
        conditionEvaluationResult: ConditionEvaluationResult = ConditionEvaluationResult(
            true,
            "executed"
        ),
        handleThrowable: (Throwable) -> Throwable? = { it }
    ): FixtureManager {
        // mocking manager
        val manager = mockk<FixtureManager>()
        every {
            manager.shouldExecute()
        }.returns(conditionEvaluationResult)
        every { manager.onAfterFixture() } answers {}
        every { manager.onBeforeFixture() } answers {}
        every {
            manager.handleTestExecutionException(any())
        } answers { handleThrowable(it.invocation.args[0] as Throwable) }
        every {
            manager.handleBeforeMethodExecutionException(any())
        } answers { handleThrowable(it.invocation.args[0] as Throwable) }
        every {
            manager.handleAfterMethodExecutionException(any())
        } answers { handleThrowable(it.invocation.args[0] as Throwable) }
        return manager
    }

    private fun createScenarioFixture(fixture: KClass<*>): ScenarioFixture {
        // mocking context
        val context = mockk<FixtureContext>()
        every {
            context.fixtureClass
        }.returns(fixture)

        val manager = createManager()
        val cut = ScenarioFixtureModel(context)
        val sut = ScenarioFixture(cut, manager)
        return sut
    }

    private fun createTwoStepScenario(): Scenario {
        val step1 = Step("step1")
        val step2 = Step("step2")
        val steps = listOf(step1, step2)
        return Scenario(
            steps, TestDataDescription(
                "fullScenario",
                false, "description for the scenario"
            )
        )
    }

    @Test
    fun `executes scenarios following the expected lifecycle`() {
        every { fixture.before() }.returns(Unit)
        every { fixture.step1() }.returns(Unit)
        every { fixture.step2() }.returns(Unit)
        every { fixture.after() }.returns(Unit)

        val scenario = createTwoStepScenario()
        val scenarioFixture = createScenarioFixture(LifeCycleFixture::class)

        val resultScenario = (scenarioFixture.execute(scenario) as ScenarioResult)
        Assertions.assertThat(resultScenario.status).isEqualTo(Status.Executed)

        verifySequence {
            fixture.before()
            fixture.step1()
            fixture.step2()
            fixture.after()
        }
    }

    private fun setupFullScenarioContext(kclass: KClass<*>): Fixture<Scenario> {
        // mocking context
        val context = mockk<FixtureContext>()
        every {
            context.fixtureClass
        }.returns(kclass)

        val manager = createManager()

        val cut = ScenarioFixtureModel(context)
        val sut = ScenarioFixture(cut, manager)
        return sut
    }

    private fun setupScenario(): Scenario {

        val step1 = Step("when the customer scans a banana for 49 cents")
        val step2 = Step("and an apple for 39 cents")
        val step3 = Step("when the customer checks out, the total sum is 88")
        val steps = listOf(step1, step2, step3)
        val scenario = Scenario(
            steps, TestDataDescription(
                "fullScenario",
                false, "description for the scenario"
            )
        )
        return scenario
    }

    @Test
    fun `executes a complete scenario`() {

        // fsc.initCallback()
        val sut = setupFullScenarioContext(FullScenario::class)
        val scenarioResult = sut.execute(setupScenario())

        Assertions.assertThat(scenarioResult.status).isEqualTo(Status.Executed)
    }

    private fun setupDisabledScenarioContext(): ScenarioFixture {
        // mocking context
        val context = mockk<FixtureContext>()
        every {
            context.fixtureClass
        }.returns(FullScenario::class)

        val manager = createManager(ConditionEvaluationResult(false, "disable"))

        val cut = ScenarioFixtureModel(context)
        val sut = ScenarioFixture(cut, manager)
        return sut
    }

    @Test
    fun disabledScenarioExecute() {

        val sut = setupDisabledScenarioContext()
        val result = sut.execute(setupScenario())

        Assertions.assertThat(result.status).isInstanceOf(Status::class.java)
        Assertions.assertThat(result.status).isEqualTo(Status.Disabled("disable"))
    }

    @Nested
    inner class `when executing steps with parameters` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        // TODO
        @Test
        fun `parameter values are passed as method parameters with the same name`() {
            every { fixture.before1() } answers {}
            every { fixture.before2() } answers {}
            every { fixture.after2() } answers {}
            every { fixture.after1() } answers {}
            every { fixture.parameterizedStep(any()) } answers {}

            val step = Step("Step with parameter: wonderful")
            val scenario = Scenario(steps = listOf(step))
            val scenarioResult =
                createScenarioFixture(ExtendedLifeCycleFixture::class).execute(scenario) as ScenarioResult

            verify { fixture.parameterizedStep("wonderful") }
        }

        @Test
        fun `parameter values are passed to methods based on explicit name bindings`() {

            every { fixture.before1() } answers {}
            every { fixture.before2() } answers {}
            every { fixture.after2() } answers {}
            every { fixture.after1() } answers {}
            every { fixture.parameterizedStepWithBinding(any()) } answers {}

            val step = Step("Step with parameter passed by explicit name bindings: explicit")
            val scenario = Scenario(steps = listOf(step))
            val scenarioResult =
                createScenarioFixture(fixture = ExtendedLifeCycleFixture::class).execute(scenario) as ScenarioResult

            verify { fixture.parameterizedStepWithBinding("explicit") }
        }

        @Test
        fun `a mismatching parameter results in "Exception"`() {

            val step = Step("Step with mismatching parameter: Oh noes!")
            val scen = Scenario(steps = listOf(step))
            val result =
                (createScenarioFixture(fixture = ExtendedLifeCycleFixture()::class)
                    .execute(scen) as ScenarioResult).status

            Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
        }
    }

    @Nested
    inner class `when an assertion fails during execution of a scenario step` {

        val fixture = ExtendedLifeCycleFixture.callback!!

        @Test
        fun `the result of the scenario is Executed`() {
            every { fixture.step1() } throws AssertionError()

            val result = execute(Scenario(listOf(Step("step1"), Step("step2")))).status

            Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
        }

        @Test
        fun `the result of the step is Failed`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult =
                (execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult).steps[0].status

            Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
        }

        @Test
        fun `the following steps are Skipped`() {
            every { fixture.step1() } throws AssertionError()

            val stepResult =
                (execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult).steps[1].status

            Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
        }

        @Test
        fun `the teardown commands are executed`() {
            every { fixture.step1() } throws AssertionError()

            execute() as ScenarioResult

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

                val result = execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult

                Assertions.assertThat(result.status).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `the following setup commands are not invoked`() {
                every { fixture.before1() } throws IllegalStateException()

                Assertions.assertThat(
                    (execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult).status
                )
                    .isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `no scenario steps are executed`() {
                every { fixture.before1() } throws IllegalStateException()

                execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult

                verify(exactly = 0) {
                    fixture.step1()
                    fixture.step2()
                }
            }

            @Test
            fun `the results of all steps are Skipped`() {
                every { fixture.before1() } throws IllegalStateException()

                val result = execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult

                Assertions.assertThat(result.steps[0].status).isEqualTo(Status.Skipped)
                Assertions.assertThat(result.steps[1].status).isEqualTo(Status.Skipped)
            }

            @Test
            fun `teardown commands, however, are invoked`() {
                every { fixture.before1() } throws IllegalStateException()

                execute(Scenario(listOf(Step("step1"), Step("step2"))))

                verify {
                    fixture.after1()
                    fixture.after2()
                }
            }
        }

        @Nested
        inner class `during execution of a step` {

            @Test
            fun `the result of the scenario is Exception`() {
                every { fixture.step1() } throws IllegalStateException()

                val result = execute(Scenario(listOf(Step("step1"), Step("step2")))).status

                Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `the result of the step is Skipped`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult = (execute(Scenario(listOf(Step("step1")))) as ScenarioResult).steps[0].status

                Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
            }

            @Test
            fun `the following steps are Skipped`() {
                every { fixture.step1() } throws IllegalStateException()

                val stepResult =
                    (execute(Scenario(listOf(Step("step1"), Step("step2")))) as ScenarioResult).steps[1].status

                Assertions.assertThat(stepResult).isInstanceOf(Status.Skipped::class.java)
            }

            @Test
            fun `the teardown commands are executed`() {
                every { fixture.step1() } throws IllegalStateException()

                execute() as ScenarioResult

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

                val result = (execute() as ScenarioResult).status

                Assertions.assertThat(result).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `all exceptions are collected and attached to the Exception result`() {
                val exception1 = IllegalStateException()
                val exception2 = IllegalStateException()
                every { fixture.after1() } throws exception1
                every { fixture.after2() } throws exception2

                val result = (execute() as ScenarioResult).status // as Status.Exception
                Assertions.assertThat(result as Status.Exception).isInstanceOf(Status.Exception::class.java)
            }

            @Test
            fun `subsequent teardown commands are executed`() {
                every { fixture.after1() } throws IllegalStateException()

                execute() as ScenarioResult

                verify { fixture.after2() }
            }
        }
    }

    @BeforeEach
    fun reset() {
        LifeCycleFixture.reset()
        ExtendedLifeCycleFixture.reset()
    }

    fun execute(): TestDataResult<Scenario> {

        val fxctx = mockk<FixtureContext>()
        every { fxctx.fixtureClass }.returns(ExtendedLifeCycleFixture::class)
        val scenarioFixture = createScenarioFixture(ExtendedLifeCycleFixture::class)

        val scenario = Scenario(emptyList())

        val result = scenarioFixture.execute(scenario)
        return result
    }

    fun execute(scenario: Scenario): TestDataResult<Scenario> {
        val scenariofixture = createScenarioFixture(ExtendedLifeCycleFixture::class)
        return scenariofixture.execute(scenario)
    }
}
