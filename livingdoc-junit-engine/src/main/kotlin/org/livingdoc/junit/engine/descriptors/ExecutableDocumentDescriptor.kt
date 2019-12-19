package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
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
import org.livingdoc.engine.execution.examples.TestDataResult
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

        result.results.mapIndexed<TestDataResult, TestDescriptor> { index, exampleResult ->
            when (exampleResult) {
                is DecisionTableResult -> DecisionTableTestDescriptor.from(uniqueId, index, exampleResult)
                is ScenarioResult -> ScenarioTestDescriptor.from(uniqueId, index, exampleResult)
                else -> throw IllegalArgumentException("Unknown Result Type $exampleResult")
            }
        }.onEach { it.setParent(this) }.forEach { dynamicTestExecutor.execute(it) }

        return context
    }

    override fun shouldBeSkipped(context: LivingDocContext?): Node.SkipResult {
        return when (val status = result.documentStatus) {
            is Status.Disabled -> skip(status.reason)
            else -> doNotSkip()
        }
    }
}
