package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor.Type
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.junit.platform.engine.support.hierarchical.Node.DynamicTestExecutor
import org.livingdoc.junit.engine.EngineExecutionContext

class ExecutableDocumentDescriptor(
    uniqueId: UniqueId,
    private val documentClass: Class<*>
) : AbstractTestDescriptor(uniqueId, documentClass.name), Node<EngineExecutionContext> {

    override fun getType(): Type {
        return Type.TEST
    }

    override fun prepare(context: EngineExecutionContext): EngineExecutionContext {
        println("ExecutableDocumentTestDescriptor: prepare context=[$context]")
        return context
    }

    override fun before(context: EngineExecutionContext): EngineExecutionContext {
        println("ExecutableDocumentTestDescriptor: before with context=[$context]")
        return context
    }

    override fun execute(
        context: EngineExecutionContext,
        dynamicTestExecutor: DynamicTestExecutor
    ): EngineExecutionContext {
        println("ExecutableDocumentTestDescriptor: execute with context=[$context] and dynamicTestExecutor=[$dynamicTestExecutor]")
        // TODO: are examples within a document dynamic tests?
        context.livingDoc.execute(documentClass)
        return context
    }

    override fun after(context: EngineExecutionContext?) {
        println("ExecutableDocumentTestDescriptor: after with context=[$context]")
    }

}
