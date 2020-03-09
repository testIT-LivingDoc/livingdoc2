package org.livingdoc.jvm.scenario

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step

internal class ScenarioFixtureFactoryTest {

    private lateinit var cut: FixtureFactory<Scenario>

    @BeforeEach
    fun `initialize factory`() {
        cut = ScenarioFixtureFactory()
    }

    @Test
    fun `can handle scenarios`() {
        assertThat(cut.isCompatible(Scenario(listOf())))
    }

    @Test
    fun `empty scenario matches all fixtures`() {
        val scenario = Scenario(listOf())

        assertThat(cut.match(EmptyFixture::class, scenario)).isTrue()
        assertThat(cut.match(CalculatorFixture::class, scenario)).isTrue()
    }

    @Test
    fun `scenario with steps does not match an empty fixture`() {
        val scenario = Scenario(
            listOf(
                Step("a calculator"),
                Step("I add 2 and 3"),
                Step("I get 5")
            )
        )

        assertThat(cut.match(EmptyFixture::class, scenario)).isFalse()
    }

    @Test
    fun `scenario with steps matches a fixture with correct step definitions`() {
        val scenario = Scenario(
            listOf(
                Step("a calculator"),
                Step("I add 2 and 3"),
                Step("I get 5")
            )
        )

        assertThat(cut.match(CalculatorFixture::class, scenario)).isTrue()
    }

    @Test
    fun `scenario with steps does not match fixture with incorrect step definitions`() {
        val scenario = Scenario(
            listOf(
                Step("a buffer"),
                Step("I append 26"),
                Step("I get 26")
            )
        )

        assertThat(cut.match(CalculatorFixture::class, scenario)).isFalse()
    }
}
