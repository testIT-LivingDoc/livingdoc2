package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.results.TestDataResult
import kotlin.reflect.KClass

class DecisionTableFixture(
    val fixtureClass: KClass<*>,
    val manager: FixtureExtensionsInterface
) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        TODO("not implemented")
    }
}
