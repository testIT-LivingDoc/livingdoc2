package org.livingdoc.jvm.engine.extension.context

import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import kotlin.reflect.KClass

internal class DocumentFixtureContextImpl(
    override val documentFixtureClass: KClass<*>,
    override val groupContext: GroupContext
) : ContextImpl<ExtensionContext>(),
    DocumentFixtureContext {

    override val parent: GroupContext?
        get() = groupContext

    override val testClass: KClass<*> get() = documentFixtureClass
}
