package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

class DocumentFixtureContextImpl(
    override val documentFixtureClass: KClass<*>,
    override val groupContext: GroupContext
) : ContextImpl<ExtensionContext>(), DocumentFixtureContext {

    override val parent: GroupContext?
        get() = groupContext

    override val testClass: KClass<*> get() = documentFixtureClass
}
