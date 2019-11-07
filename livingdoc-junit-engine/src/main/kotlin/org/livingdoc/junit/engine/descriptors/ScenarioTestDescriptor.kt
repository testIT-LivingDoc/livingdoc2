package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.doNotSkip
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.skip
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.junit.engine.LivingDocContext

class ScenarioTestDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    private val scenarioResult: ScenarioResult
) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        scenarioResult.steps.forEachIndexed { index, stepResult ->
            val descriptor = StepTestDescriptor(stepUniqueId(index), stepDisplayName(stepResult), stepResult)
                .also { it.setParent(this) }
            dynamicTestExecutor.execute(descriptor)
        }
        return context
    }

    private fun stepUniqueId(index: Int) = uniqueId.append("step", "$index")
    private fun stepDisplayName(stepResult: StepResult) = stepResult.value

    override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
        return when (val result = scenarioResult.status) {
            Status.Unknown -> skip("unknown")
            is Status.Disabled -> skip(result.reason)
            Status.Skipped -> skip("skipped")
            else -> doNotSkip()
        }
    }

    class StepTestDescriptor(
        uniqueId: UniqueId,
        displayName: String,
        private val stepResult: StepResult
    ) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

        override fun getType() = TestDescriptor.Type.TEST

        override fun execute(
            context: LivingDocContext,
            dynamicTestExecutor: Node.DynamicTestExecutor
        ): LivingDocContext {
            val result = stepResult.status
            when (result) {
                is Status.Failed -> throw result.reason
                is Status.Exception -> throw result.exception
            }
            return context
        }

        override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
            return when (val result = stepResult.status) {
                Status.Unknown -> skip("unknown")
                is Status.Disabled -> skip(result.reason)
                Status.Skipped -> skip("skipped")
                else -> doNotSkip()
            }
        }
    }
}
