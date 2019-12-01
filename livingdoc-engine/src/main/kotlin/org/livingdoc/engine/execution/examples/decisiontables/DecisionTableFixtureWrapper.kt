package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.fixtures.FixtureWrapper
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.decisiontable.DecisionTable

class DecisionTableFixtureWrapper(
    val fixtureClass: Class<*>
) : FixtureWrapper {

    fun execute(testData: DecisionTable): DecisionTableResult {
        return DecisionTableExecution(this.fixtureClass, testData, null).execute()
    }

    override fun execute(testData: TestData): TestDataResult {
        throw TestDataNoDecisionTableException(testData, this.fixtureClass)
    }


    class TestDataNoDecisionTableException(testData: TestData, fixtureClass: Class<*>) :
        RuntimeException(
            "The given TestData is a ${testData.javaClass.canonicalName}" +
                    " and not a DecisionTable and can not be used to execute this fixture: $fixtureClass"
        )
}
