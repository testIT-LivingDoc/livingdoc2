package org.livingdoc.jvm.engine.extension.context

import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import kotlin.reflect.KClass

internal class GroupContextImpl(override val groupClass: KClass<*>) : ContextImpl<ExtensionContext>(),
    GroupContext {
    override val testClass: KClass<*>
        get() = groupClass
}
