package org.livingdoc.jvm.scenario

import org.junit.jupiter.api.Test
import org.livingdoc.api.fixtures.scenarios.Step

@Test
fun `Scenario Fixture`() {
}

internal class EmptyFixture

internal class CalculatorFixture {
    @Step("a calculator")
    fun setup() {}

    @Step("I add 2 and 3")
    fun add() {}

    @Step("I get 5")
    fun verify() {}
}
