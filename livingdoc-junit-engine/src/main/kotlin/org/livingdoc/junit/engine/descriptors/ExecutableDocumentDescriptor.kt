package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor.Type
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.DynamicTestExecutor
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.doNotSkip
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.skip
import org.livingdoc.engine.execution.documents.DocumentResult
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.junit.engine.LivingDocContext
import org.livingdoc.reports.ReportsManager

class ExecutableDocumentDescriptor(
    uniqueId: UniqueId,
    private val result: DocumentResult
) : AbstractTestDescriptor(uniqueId, result.documentClass.name, ClassSource.from(result.documentClass)),
    Node<LivingDocContext> {

    override fun getType() = Type.CONTAINER

    override fun mayRegisterTests() = true

    override fun execute(context: LivingDocContext, dynamicTestExecutor: DynamicTestExecutor): LivingDocContext {
        val reportsManager = ReportsManager.from(context.livingDoc.configProvider)
        reportsManager.generateReports(result)

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
                        ScenarioTestDescriptor(scenarioUniqueId(index), scenarioDisplayName(index), exampleResult)
                            .also { it.setParent(this) }
                    dynamicTestExecutor.execute(descriptor)
                }
            }
        }

        return context
    }

    override fun shouldBeSkipped(context: LivingDocContext?): Node.SkipResult {
        return when (val status = result.documentStatus) {
            is Status.Disabled -> skip(status.reason)
            else -> doNotSkip()
        }
    }

    private fun tableUniqueId(index: Int) = uniqueId.append("table", "$index")
    private fun tableDisplayName(index: Int) = "Table #${index + 1}"

    private fun scenarioUniqueId(index: Int) = uniqueId.append("scenario", "$index")
    private fun scenarioDisplayName(index: Int) = "Scenario #${index + 1}"
}
