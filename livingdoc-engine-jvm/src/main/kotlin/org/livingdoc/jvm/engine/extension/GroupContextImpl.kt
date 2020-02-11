package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

class GroupContextImpl(override val groupClass: KClass<*>) : ContextImpl(), GroupContext
