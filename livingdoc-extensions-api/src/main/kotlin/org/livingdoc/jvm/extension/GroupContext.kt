package org.livingdoc.jvm.extension

import org.livingdoc.engine.Group
import kotlin.reflect.KClass

interface GroupContext {
    fun getGroupClass(): KClass<*>

    fun getGroup(): Group
}
