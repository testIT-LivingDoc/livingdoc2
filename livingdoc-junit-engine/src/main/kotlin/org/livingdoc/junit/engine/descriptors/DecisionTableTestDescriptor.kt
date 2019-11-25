package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.doNotSkip
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.skip
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.junit.engine.LivingDocContext
import org.livingdoc.repositories.model.decisiontable.Header

class DecisionTableTestDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    private val tableResult: DecisionTableResult
) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        tableResult.rows.forEachIndexed { index, rowResult ->
            val descriptor = RowTestDescriptor(rowUniqueId(index), rowDisplayName(index), rowResult)
                .also { it.setParent(this) }
            dynamicTestExecutor.execute(descriptor)
        }
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
            rowResult.headerToField.forEach { header, fieldResult ->
                val descriptor =
                    FieldTestDescriptor(fieldUniqueId(header), fieldDisplayName(header, fieldResult), fieldResult)
                        .also { it.setParent(this) }
                dynamicTestExecutor.execute(descriptor)
            }
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
            private val fieldResult: FieldResult
        ) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

            override fun getType() = TestDescriptor.Type.TEST

            override fun execute(
                context: LivingDocContext,
                dynamicTestExecutor: Node.DynamicTestExecutor
            ): LivingDocContext {
                val result = fieldResult.status
                when (result) {
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
