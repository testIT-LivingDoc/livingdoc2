package org.livingdoc.junit.engine.descriptors

import org.junit.jupiter.api.Disabled
import org.junit.platform.engine.TestDescriptor.Type
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.DynamicTestExecutor
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.doNotSkip
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.skip
import org.livingdoc.engine.ExecutableDocumentModel
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.junit.engine.LivingDocContext

class ExecutableDocumentDescriptor(
    uniqueId: UniqueId,
    private val documentClass: Class<*>
) : AbstractTestDescriptor(uniqueId, documentClass.name, ClassSource.from(documentClass)), Node<LivingDocContext> {

    override fun getType() = Type.CONTAINER

    override fun mayRegisterTests() = true

    override fun execute(context: LivingDocContext, dynamicTestExecutor: DynamicTestExecutor): LivingDocContext {
        val result = context.livingDoc.execute(documentClass)
        val documentModel = ExecutableDocumentModel.of(documentClass)

        result.results.forEachIndexed { index, exampleResult ->
            when (exampleResult) {
                is DecisionTableResult -> {
                    val descriptor =
                        DecisionTableTestDescriptor(tableUniqueId(index), tableDisplayName(index), exampleResult)
                            .also { it.setParent(this) }
                    dynamicTestExecutor.execute(descriptor)
                }
                is ScenarioResult -> {
                    val descriptor =
                        ScenarioTestDescriptor(
                            scenarioUniqueId(index),
                            scenarioDisplayName(index),
                            exampleResult,
                            documentModel.scenarioFixtures.get(index)
                        )
                            .also { it.setParent(this) }
                    dynamicTestExecutor.execute(descriptor)
                }
            }
        }

        return context
    }

    override fun shouldBeSkipped(context: LivingDocContext?): Node.SkipResult {
        if (documentClass.isAnnotationPresent(Disabled::class.java)) {
            return skip(documentClass.getAnnotation(Disabled::class.java).value)
        }

        return doNotSkip()
    }

    private fun tableUniqueId(index: Int) = uniqueId.append("table", "$index")
    private fun tableDisplayName(index: Int) = "Table #${index + 1}"

    private fun scenarioUniqueId(index: Int) = uniqueId.append("scenario", "$index")
    private fun scenarioDisplayName(index: Int) = "Scenario #${index + 1}"
}
