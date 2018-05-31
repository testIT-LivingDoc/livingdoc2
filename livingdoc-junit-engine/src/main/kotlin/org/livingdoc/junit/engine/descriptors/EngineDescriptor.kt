package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.livingdoc.junit.engine.EngineExecutionContext

class EngineDescriptor(
    uniqueId: UniqueId
) : EngineDescriptor(uniqueId, "LivingDoc"), Node<EngineExecutionContext> {

    override fun prepare(context: EngineExecutionContext): EngineExecutionContext {
        println("LivingDocEngineDescriptor: prepare context=[$context]")
        // TODO: do things to initialize LivingDoc's Engine
        return context
    }

    override fun before(context: EngineExecutionContext): EngineExecutionContext {
        println("LivingDocEngineDescriptor: before with context=[$context]")
        // TODO: do something before first document is executed
        return context
    }

    override fun after(context: EngineExecutionContext) {
        println("LivingDocEngineDescriptor: after with context=[$context]")
        // TODO: do some cleanup after the last document was executed
    }

}
