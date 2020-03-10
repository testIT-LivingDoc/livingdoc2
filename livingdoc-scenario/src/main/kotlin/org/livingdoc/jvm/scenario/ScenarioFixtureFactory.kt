package org.livingdoc.jvm.scenario

import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureManager
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.scenario.matching.NoMatchingStepTemplate
import org.livingdoc.scenario.matching.ScenarioStepMatcher
import org.livingdoc.scenario.matching.StepTemplate
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers

class ScenarioFixtureFactory : FixtureFactory<Scenario> {

    override fun isCompatible(testData: TestData): Boolean = testData is Scenario

    override fun match(fixtureClass: KClass<*>, testData: Scenario): Boolean {
        val stepMatcher = ScenarioStepMatcher(fixtureClass.declaredMembers.flatMap { member ->
            member.annotations.filterIsInstance<Step>().flatMap { it.value.asIterable() }
        }.map { StepTemplate.parse(it) })

        return testData.steps.all { step ->
            try {
                stepMatcher.match(step.value)
                true
            } catch (e: NoMatchingStepTemplate) {
                false
            }
        }
    }

    override fun getFixture(context: FixtureContext, manager: FixtureManager): Fixture<Scenario> {
        val fixtureModel = ScenarioFixtureModel(context)
        return ScenarioFixture(fixtureModel, manager)
    }
}
