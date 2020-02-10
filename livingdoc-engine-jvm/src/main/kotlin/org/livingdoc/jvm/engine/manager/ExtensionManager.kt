package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.engine.castToClass
import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.FixtureContext
import org.livingdoc.jvm.extension.GroupContext
import org.livingdoc.jvm.extension.Store
import org.livingdoc.jvm.extension.spi.Extension
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class ExtensionManager {

    private val defaultExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

    fun executeBeforeGroup(context: GroupContext) {
        val groupExtensions = context.extensions.map {
            instantiateExtension(
                it
            )
        }
        context.extensionStore.extensions = groupExtensions

        val activeExtensions = defaultExtensions + groupExtensions
        activeExtensions.forEach {
            it.onBeforeGroup(context)
        }
    }

    fun executeBeforeDocumentFixture(context: DocumentFixtureContext) {
        val documentFixtureExtensions = context.extensions.map {
            instantiateExtension(
                it
            )
        }
        context.extensionStore.extensions = documentFixtureExtensions

        val activeExtensions =
            defaultExtensions + context.groupContext.extensionStore.extensions + documentFixtureExtensions
        activeExtensions.forEach {
            it.onBeforeDocument(context)
        }
    }

    fun executeBeforeFixture(context: FixtureContext) {
        val fixtureExtensions = context.extensions.map {
            instantiateExtension(
                it
            )
        }
        context.extensionStore.extensions = fixtureExtensions

        val activeExtensions =
            defaultExtensions + context.documentFixtureContext.groupContext.extensionStore.extensions +
                    context.documentFixtureContext.extensionStore.extensions + fixtureExtensions
        activeExtensions.forEach {
            it.onBeforeFixture(context)
        }
    }

    fun executeAfterFixture(context: FixtureContext) {
        val activeExtensions =
            defaultExtensions + context.documentFixtureContext.groupContext.extensionStore.extensions +
                    context.documentFixtureContext.extensionStore.extensions + context.extensionStore.extensions
        activeExtensions.forEach {
            it.onAfterFixture(context)
        }
    }

    fun executeAfterDocumentFixture(context: DocumentFixtureContext) {
        val activeExtensions =
            defaultExtensions + context.groupContext.extensionStore.extensions + context.extensionStore.extensions
        activeExtensions.forEach {
            it.onAfterDocument(context)
        }
    }

    fun executeAfterGroup(context: GroupContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.forEach {
            it.onAfterGroup(context)
        }
    }
}

fun instantiateExtension(extensionClass: KClass<*>): Extension {
    return extensionClass.castToClass(Extension::class).createInstance()
}

private val Context.extensionStore: Store
    get() = this.getStore("org.livingdoc.jvm.engine.manager.ExtensionManager")

private val GroupContext.extensions: List<KClass<*>>
    get() = this.groupClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private val DocumentFixtureContext.extensions: List<KClass<*>>
    get() = this.documentFixtureClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private val FixtureContext.extensions: List<KClass<*>>
    get() = this.fixtureClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private var Store.extensions: List<Extension>
    get() = this["extensions"] as List<Extension>
    set(value) {
        this["extensions"] = value
    }
