package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.doNotSkip
import org.junit.platform.engine.support.hierarchical.Node.SkipResult.skip
import org.livingdoc.engine.ExecutableDecisionTable
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.junit.engine.LivingDocContext
import org.livingdoc.repositories.model.decisiontable.Header

class DecisionTableTestDescriptor(
        uniqueId: UniqueId,
        displayName: String,
        private val decisionTable: ExecutableDecisionTable
) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        decisionTable.execute().rows.forEachIndexed { index, rowResult ->
            val descriptor = RowTestDescriptor(rowUniqueId(index), rowDisplayName(index), rowResult)
                    .also { it.setParent(this) }
            dynamicTestExecutor.execute(descriptor)
        }
        return context
    }

    private fun rowUniqueId(index: Int) = uniqueId.append("row", "$index")
    private fun rowDisplayName(index: Int) = "Row #${index + 1}"

    class RowTestDescriptor(
            uniqueId: UniqueId,
            displayName: String,
            private val rowResult: RowResult
    ) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

        override fun getType() = TestDescriptor.Type.CONTAINER

        override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
            rowResult.headerToField.forEach { header, fieldResult ->
                val descriptor = FieldTestDescriptor(fieldUniqueId(header), fieldDisplayName(header, fieldResult), fieldResult)
                        .also { it.setParent(this) }
                dynamicTestExecutor.execute(descriptor)
            }
            return context
        }

        private fun fieldUniqueId(header: Header) = uniqueId.append("field", header.name)
        private fun fieldDisplayName(header: Header, fieldResult: FieldResult) = "[${header.name}] = ${fieldResult.value}"

        override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
            val result = rowResult.result
            return when (result) {
                Result.Unknown -> skip("unknown")
                Result.Skipped -> skip("skipped")
                else -> doNotSkip()
            }
        }

        class FieldTestDescriptor(
                uniqueId: UniqueId,
                displayName: String,
                private val fieldResult: FieldResult
        ) : AbstractTestDescriptor(uniqueId, displayName), Node<LivingDocContext> {

            override fun getType() = TestDescriptor.Type.TEST

            override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
                val result = fieldResult.result
                when (result) {
                    is Result.Failed -> throw result.reason
                    is Result.Exception -> throw result.exception
                }
                return context
            }

            override fun shouldBeSkipped(context: LivingDocContext): Node.SkipResult {
                val result = fieldResult.result
                return when (result) {
                    Result.Unknown -> skip("unknown")
                    Result.Skipped -> skip("skipped")
                    else -> doNotSkip()
                }
            }

        }

    }

}