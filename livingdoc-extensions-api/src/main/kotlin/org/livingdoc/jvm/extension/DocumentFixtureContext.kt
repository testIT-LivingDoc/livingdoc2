package org.livingdoc.jvm.extension

import org.livingdoc.engine.DocumentFixture
import kotlin.reflect.KClass

interface DocumentFixtureContext {
    fun getDocumentFixtureClass(): KClass<*>

    fun getDocumentFixture(): DocumentFixture

    fun getGroupContext(): GroupContext
}
