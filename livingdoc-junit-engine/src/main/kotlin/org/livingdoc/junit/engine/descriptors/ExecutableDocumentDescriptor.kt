package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor.Type
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.DynamicTestExecutor
import org.livingdoc.engine.ExecutableDecisionTable
import org.livingdoc.engine.ExecutableScenario
import org.livingdoc.junit.engine.LivingDocContext

class ExecutableDocumentDescriptor(
        uniqueId: UniqueId,
        private val documentClass: Class<*>
) : AbstractTestDescriptor(uniqueId, documentClass.name), Node<LivingDocContext> {

    override fun getType() = Type.CONTAINER_AND_TEST

    override fun execute(context: LivingDocContext, dynamicTestExecutor: DynamicTestExecutor): LivingDocContext {
        context.livingDoc
                .getExecutableExamples(documentClass)
                .forEachIndexed { index, example ->
                    when (example) {
                        is ExecutableDecisionTable -> execute(index, dynamicTestExecutor, example)
                        is ExecutableScenario -> execute(index, dynamicTestExecutor, example)
                    }
                }
        return context
    }

    private fun execute(index: Int, dynamicTestExecutor: DynamicTestExecutor, example: ExecutableScenario) {
        val descriptor = ScenarioTestDescriptor(scenarioUniqueId(index), scenarioDisplayName(index), example)
                .also { it.setParent(this) }
        dynamicTestExecutor.execute(descriptor)
    }

    private fun execute(index: Int, dynamicTestExecutor: DynamicTestExecutor, example: ExecutableDecisionTable) {
        val descriptor = DecisionTableTestDescriptor(tableUniqueId(index), tableDisplayName(index), example)
                .also { it.setParent(this) }
        dynamicTestExecutor.execute(descriptor)
    }

    private fun tableUniqueId(index: Int) = uniqueId.append("table", "$index")
    private fun tableDisplayName(index: Int) = "Table #${index + 1}"

    private fun scenarioUniqueId(index: Int) = uniqueId.append("scenario", "$index")
    private fun scenarioDisplayName(index: Int) = "Scenario #${index + 1}"

}