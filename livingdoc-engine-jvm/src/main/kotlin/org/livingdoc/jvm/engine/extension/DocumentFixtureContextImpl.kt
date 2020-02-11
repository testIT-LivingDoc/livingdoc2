package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

class DocumentFixtureContextImpl(
    override val documentFixtureClass: KClass<*>,
    override val groupContext: GroupContext
) : ContextImpl(), DocumentFixtureContext {

    override val parent: Context?
        get() = groupContext
}
