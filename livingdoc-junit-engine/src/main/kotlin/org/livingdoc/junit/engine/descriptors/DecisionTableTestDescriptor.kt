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
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.decisiontables.FieldResult
import org.livingdoc.results.examples.decisiontables.RowResult

class DecisionTableTestDescriptor private constructor(
    uniqueId: UniqueId,
    displayName: String,
    private val tableResult: DecisionTableResult,
    testSource: TestSource?
) : AbstractTestDescriptor(uniqueId, displayName, testSource), Node<LivingDocContext> {

    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        tableResult.rows.mapIndexed { index, rowResult ->
            RowTestDescriptor(rowUniqueId(index), rowDisplayName(index), rowResult)
        }.onEach { it.setParent(this) }.forEach { dynamicTestExecutor.execute(it) }
        return context
    }

    private fun rowUniqueId(index: Int) = uniqueId.append("row", "$index")
    private fun rowDisplayName(index: Int) = "Row #${index + 1}"

    override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
        return when (val result = tableResult.status) {
            Status.Unknown -> skip("unknown")
            is Status.Disabled -> skip(result.reason)
            Status.Skipped -> skip("skipped")
            Status.Manual -> skip("manual")
            else -> doNotSkip()
        }
    }

    companion object {
        /**
         * Create a new [DecisionTableTestDescriptor] from the [uniqueId] and the [index] representing the [result].
         */
        fun from(uniqueId: UniqueId, index: Int, result: DecisionTableResult): DecisionTableTestDescriptor {
            return DecisionTableTestDescriptor(
                tableUniqueId(uniqueId, index),
                tableDisplayName(index),
                result,
                result.fixtureSource?.let { ClassSource.from(it) }
            )
        }

        private fun tableUniqueId(uniqueId: UniqueId, index: Int) = uniqueId.append("table", "$index")
        private fun tableDisplayName(index: Int) = "Table #${index + 1}"
    }

    class RowTestDescriptor(
        uniqueId: UniqueId,
        displayName: String,
        private val rowResult: RowResult
    ) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

        override fun getType() = TestDescriptor.Type.CONTAINER

        override fun execute(
            context: LivingDocContext,
            dynamicTestExecutor: Node.DynamicTestExecutor
        ): LivingDocContext {
            rowResult.headerToField.map { (header, fieldResult) ->
                FieldTestDescriptor(
                    fieldUniqueId(header),
                    fieldDisplayName(header, fieldResult),
                    fieldResult,
                    fieldResult.method?.let { MethodSource.from(it) }
                )
            }.onEach { it.setParent(this) }.forEach { dynamicTestExecutor.execute(it) }
            return context
        }

        private fun fieldUniqueId(header: Header) = uniqueId.append("field", header.name)
        private fun fieldDisplayName(header: Header, fieldResult: FieldResult) =
            "[${header.name}] = ${fieldResult.value}"

        override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
            return when (val result = rowResult.status) {
                Status.Unknown -> skip("unknown")
                is Status.Disabled -> skip(result.reason)
                Status.Skipped -> skip("skipped")
                else -> doNotSkip()
            }
        }

        class FieldTestDescriptor(
            uniqueId: UniqueId,
            displayName: String,
            private val fieldResult: FieldResult,
            testSource: TestSource?
        ) : AbstractTestDescriptor(uniqueId, displayName, testSource), Node<LivingDocContext> {

            override fun getType() = TestDescriptor.Type.TEST

            override fun execute(
                context: LivingDocContext,
                dynamicTestExecutor: Node.DynamicTestExecutor
            ): LivingDocContext {
                when (val result = fieldResult.status) {
                    is Status.Failed -> throw result.reason
                    is Status.Exception -> throw result.exception
                }
                return context
            }

            override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
                return when (val result = fieldResult.status) {
                    Status.Unknown -> skip("unknown")
                    is Status.Disabled -> skip(result.reason)
                    Status.Skipped -> skip("skipped")
                    else -> doNotSkip()
                }
            }
        }
    }
}
