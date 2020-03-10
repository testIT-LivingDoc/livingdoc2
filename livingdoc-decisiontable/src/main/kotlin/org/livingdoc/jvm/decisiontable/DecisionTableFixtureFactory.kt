package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureManager
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation

class DecisionTableFixtureFactory : FixtureFactory<DecisionTable> {

    override fun isCompatible(testData: TestData): Boolean = testData is DecisionTable

    override fun match(fixtureClass: KClass<*>, testData: DecisionTable): Boolean {
        DecisionTableFixtureChecker.check(DecisionTableFixtureModel(fixtureClass))

        val headerNames = testData.headers.map { it.name }
        val numberOfHeaders = headerNames.size
        val inputAliasMethod = fixtureClass.declaredMembers.mapNotNull { member ->
            member.findAnnotation<Input>()?.value
        }

        val checkAliasMethod = fixtureClass.declaredMembers.flatMap { member ->
            member.annotations.filterIsInstance<Check>().map { it.value }
        }
        val aliases = inputAliasMethod + checkAliasMethod
        val numberOfMatchedHeaders = headerNames.filter { aliases.contains(it) }.size

        return numberOfHeaders == numberOfMatchedHeaders && aliases.size == numberOfMatchedHeaders
    }

    override fun getFixture(context: FixtureContext, manager: FixtureManager): Fixture<DecisionTable> {
        val model = DecisionTableFixtureModel(context.fixtureClass)
        return DecisionTableFixture(model, manager)
    }
}
