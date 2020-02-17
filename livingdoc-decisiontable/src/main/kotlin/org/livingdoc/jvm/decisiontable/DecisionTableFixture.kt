package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.results.TestDataResult
import kotlin.reflect.KClass

class DecisionTableFixture(val fixtureClass: KClass<*>) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        TODO("not implemented")
    }
}
