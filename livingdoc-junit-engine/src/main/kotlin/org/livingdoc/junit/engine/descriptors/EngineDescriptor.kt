package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.livingdoc.junit.engine.LivingDocContext
import kotlin.reflect.KClass

class EngineDescriptor(uniqueId: UniqueId, private val documentClasses: List<KClass<*>> = listOf()) :
    EngineDescriptor(uniqueId, "LivingDoc"), Node<LivingDocContext> {
    override fun getType() = TestDescriptor.Type.CONTAINER

    override fun mayRegisterTests() = true

    override fun execute(context: LivingDocContext, dynamicTestExecutor: Node.DynamicTestExecutor): LivingDocContext {
        context.livingDoc.execute(documentClasses).flatMap { it.documentResults }.forEach { documentResult ->
            // TODO handle groupResults
            val documentDescriptor = ExecutableDocumentDescriptor(
                uniqueId(uniqueId, documentResult.documentClass),
                documentResult
            )

            dynamicTestExecutor.execute(documentDescriptor)
        }

        return context
    }

    private fun uniqueId(uniqueId: UniqueId, documentClass: Class<*>): UniqueId {
        return uniqueId.append("documentClass", documentClass.name)
    }
}
