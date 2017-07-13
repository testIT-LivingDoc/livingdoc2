package org.livingdoc.engine.execution.examples.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.examples.scenarios.matching.ScenarioFixtureChecker
import kotlin.reflect.KClass

class ScenarioFixtureCheckerTest {

    val classPrefix = "org.livingdoc.engine.execution.examples.scenarios.MalformedFixtures"
    val methodPrefix = "void ${classPrefix}\$"

    @Test fun `fixture must have default constructor`() {
        val errors = executeCheck(MalformedFixtures.NoDefaultConstructor::class)
        assertThat(errors).containsExactly("The fixture class <$classPrefix.NoDefaultConstructor> has no default constructor!")
    }

    @Test fun `before methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.BeforeWithParameter::class)
        assertThat(errors).containsExactly("@Before method <${methodPrefix}BeforeWithParameter.before(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `before methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticBeforeMethod::class)
        assertThat(errors).containsExactly("@Before method <static ${methodPrefix}StaticBeforeMethod.before()> must not be static!")
    }

    @Test fun `step methods cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.StepMethodsWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <step> is used multiple times!")
    }

    @Test fun `step methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticStepMethod::class)
        assertThat(errors).containsExactly("@Step method <static ${methodPrefix}StaticStepMethod.step()> must not be static!")
    }

    @Test fun `step templates and annotated methods must have same number of parameters`() {
        val errors = executeCheck(MalformedFixtures.MismatchingParameterNumber::class)
        assertThat(errors).containsExactly("Method <${methodPrefix}MismatchingParameterNumber.step()> is annotated with step template with wrong parameter count: 'step with {parameter}'")
    }

    @Test fun `after methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.AfterWithParameter::class)
        assertThat(errors).containsExactly("@After method <${methodPrefix}AfterWithParameter.after(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `after methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticAfterMethod::class)
        assertThat(errors).containsExactly("@After method <static ${methodPrefix}StaticAfterMethod.after()> must not be static!")
    }

    private fun executeCheck(fixtureClass: KClass<*>): List<String> {
        val model = ScenarioFixtureModel(fixtureClass.java)
        return ScenarioFixtureChecker.check(model)
    }

}