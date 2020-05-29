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
import org.livingdoc.junit.engine.LivingDocContext
import org.livingdoc.results.Status
import org.livingdoc.results.examples.scenarios.ScenarioResult
import org.livingdoc.results.examples.scenarios.StepResult

class ScenarioTestDescriptor private constructor(
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
                stepResult.fixtureMethod?.let { MethodSource.from(it) }
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
        /**
         * Create a new [ScenarioTestDescriptor] from the [uniqueId] and with the given [index] representing the
         * [result].
         */
        fun from(uniqueId: UniqueId, index: Int, result: ScenarioResult): ScenarioTestDescriptor {
            return ScenarioTestDescriptor(
                scenarioUniqueId(uniqueId, index),
                scenarioDisplayName(index),
                result,
                result.fixtureSource?.let { ClassSource.from(it) }
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
