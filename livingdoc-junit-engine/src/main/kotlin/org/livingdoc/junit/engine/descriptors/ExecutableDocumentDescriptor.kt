package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor.Type
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.DynamicTestExecutor
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableExecutor
import org.livingdoc.engine.execution.examples.scenarios.ScenarioExecutor
import org.livingdoc.junit.engine.LivingDocContext
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step

class ExecutableDocumentDescriptor(
        uniqueId: UniqueId,
        private val documentClass: Class<*>
) : AbstractTestDescriptor(uniqueId, documentClass.name), Node<LivingDocContext> {

    override fun getType() = Type.CONTAINER_AND_TEST

    override fun execute(context: LivingDocContext, dynamicTestExecutor: DynamicTestExecutor): LivingDocContext {
        val result = context.livingDoc.execute(documentClass)
        // val result = dummyExecution()

        result.decisionTableResults.forEachIndexed { index, tableResult ->
            val descriptor = DecisionTableTestDescriptor(tableUniqueId(index), tableDisplayName(index), tableResult)
                    .also { it.setParent(this) }
            dynamicTestExecutor.execute(descriptor)
        }

        result.scenarioResults.forEachIndexed { index, scenarioResult ->
            val descriptor = ScenarioTestDescriptor(scenarioUniqueId(index), scenarioDisplayName(index), scenarioResult)
                    .also { it.setParent(this) }
            dynamicTestExecutor.execute(descriptor)
        }

        return context
    }

    private fun tableUniqueId(index: Int) = uniqueId.append("table", "$index")
    private fun tableDisplayName(index: Int) = "Table #${index + 1}"

    private fun scenarioUniqueId(index: Int) = uniqueId.append("scenario", "$index")
    private fun scenarioDisplayName(index: Int) = "Scenario #${index + 1}"

    // TODO: remove as soon as it is no longer need
    private fun dummyExecution(): DocumentResult {

        val headerA = Header("a")
        val headerB = Header("b")
        val headerAplusB = Header("a + b = ?")
        val headerAminusB = Header("a - b = ?")
        val headerAtimesB = Header("a * b = ?")
        val headerAdividedByB = Header("a / b = ?")
        val decisionTable = DecisionTable(
                listOf(headerA, headerB, headerAplusB, headerAminusB, headerAtimesB, headerAdividedByB),
                listOf(
                        Row(mapOf(
                                headerA to Field("10"),
                                headerB to Field("5"),
                                headerAplusB to Field("15"),
                                headerAminusB to Field("5"),
                                headerAtimesB to Field("50"),
                                headerAdividedByB to Field("2")
                        )),
                        Row(mapOf(
                                headerA to Field("-3"),
                                headerB to Field("2"),
                                headerAplusB to Field("-1"),
                                headerAminusB to Field("-5"),
                                headerAtimesB to Field("-6"),
                                headerAdividedByB to Field("-1.5")
                        ))
                )
        )
        val decisionTableFixture = documentClass.declaredClasses
                .single { it.isAnnotationPresent(DecisionTableFixture::class.java) }
        val decisionTableResult = DecisionTableExecutor()
                .execute(decisionTable, decisionTableFixture, documentClass)

        val scenario = Scenario(listOf(
                Step("adding 1 and 2 equals 3"),
                Step("subtraction 5 form 15 equals 10"),
                Step("multiplying 3 and 6 equals 18"),
                Step("dividing 20 by 5 equals 4")
        ))
        val scenarioFixture = documentClass.declaredClasses
                .single { it.isAnnotationPresent(ScenarioFixture::class.java) }
        val scenarioResult = ScenarioExecutor()
                .execute(scenario, scenarioFixture, documentClass)

        return DocumentResult(listOf(decisionTableResult), listOf(scenarioResult))

    }

}