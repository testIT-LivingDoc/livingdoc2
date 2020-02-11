package org.livingdoc.jvm.scenario

import org.livingdoc.jvm.extension.Fixture
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.TestDataResult
import kotlin.reflect.KClass

class ScenarioFixture(val fixtureClass: KClass<*>) : Fixture<Scenario> {
    override fun execute(testData: Scenario): TestDataResult<Scenario> {
        TODO("not implemented")
    }
}
