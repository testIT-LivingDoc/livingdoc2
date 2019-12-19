package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
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
    private val scenarioResult: ScenarioResult,
    testSource: TestSource?
) : AbstractTestDescriptor(uniqueId, displayName, testSource), Node<LivingDocContext> {

    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        scenarioResult.steps.mapIndexed { index, stepResult ->
            StepTestDescriptor(
                stepUniqueId(index),
                stepDisplayName(stepResult),
                stepResult,
                stepResult.fixtureMethod.map { MethodSource.from(it) }.orElse(null)
            )
        }.onEach { it.setParent(this) }.forEach { dynamicTestExecutor.execute(it) }
        return context
    }

    private fun stepUniqueId(index: Int) = uniqueId.append("step", "$index")
    private fun stepDisplayName(stepResult: StepResult) = stepResult.value

    override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
        return when (val result = scenarioResult.status) {
            Status.Unknown -> skip("unknown")
            is Status.Disabled -> skip(result.reason)
            Status.Skipped -> skip("skipped")
            Status.Manual -> skip("manual")
            else -> doNotSkip()
        }
    }

    companion object {
        fun from(uniqueId: UniqueId, index: Int, result: ScenarioResult): ScenarioTestDescriptor {
            return ScenarioTestDescriptor(
                scenarioUniqueId(uniqueId, index),
                scenarioDisplayName(index),
                result,
                result.fixtureSource.map { ClassSource.from(it) }.orElse(null)
            )
        }

        private fun scenarioUniqueId(uniqueId: UniqueId, index: Int) = uniqueId.append("scenario", "$index")
        private fun scenarioDisplayName(index: Int) = "Scenario #${index + 1}"
    }

    class StepTestDescriptor(
        uniqueId: UniqueId,
        displayName: String,
        private val stepResult: StepResult,
        testSource: TestSource?
    ) : AbstractTestDescriptor(uniqueId, displayName, testSource), Node<LivingDocContext> {

        override fun getType() = TestDescriptor.Type.TEST

        override fun execute(
            context: LivingDocContext,
            dynamicTestExecutor: Node.DynamicTestExecutor
        ): LivingDocContext {
            when (val result = stepResult.status) {
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
