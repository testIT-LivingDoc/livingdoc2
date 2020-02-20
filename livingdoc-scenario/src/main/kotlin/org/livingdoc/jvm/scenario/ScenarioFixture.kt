package org.livingdoc.jvm.scenario

import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.TestDataResult
import kotlin.reflect.KClass

class ScenarioFixture(
    val fixtureClass: KClass<*>,
    private val manager: FixtureExtensionsInterface
) :
    Fixture<Scenario> {
    override fun execute(testData: Scenario): TestDataResult<Scenario> {
        TODO("not implemented")
    }
}
