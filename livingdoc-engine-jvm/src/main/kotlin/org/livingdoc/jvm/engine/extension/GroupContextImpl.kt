package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

internal class GroupContextImpl(override val groupClass: KClass<*>) : ContextImpl<ExtensionContext>(), GroupContext {
    override val testClass: KClass<*>
        get() = groupClass
}
