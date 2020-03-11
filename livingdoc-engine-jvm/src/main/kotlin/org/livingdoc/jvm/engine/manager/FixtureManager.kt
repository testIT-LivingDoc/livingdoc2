package org.livingdoc.jvm.engine.manager

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureAnnotation
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.jvm.engine.EngineContext
import org.livingdoc.jvm.engine.extension.context.FixtureContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionRegistryImpl.Companion.loadExtensions
import org.livingdoc.repositories.model.TestData
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

internal class FixtureManager {
    private val fixtureFactories: List<FixtureFactory<*>> =
        ServiceLoader.load(FixtureFactory::class.java).iterator().asSequence().toList()

    /**
     * Get the fixture for the given [testData] using the provided [context]. The Context must be have a
     * DocumentFixtureContext.
     */
    fun <T : TestData> getFixture(context: EngineContext, testData: T, extensionManager: ExtensionManager): Fixture<T> {
        val extensionContext = context.extensionContext as DocumentFixtureContext
        val fixtureClasses = extensionContext.externalFixtureClasses + extensionContext.internalFixtureClasses
        if (fixtureClasses.isEmpty()) {
            throw IllegalArgumentException("There are no Fixtures for ${extensionContext.documentFixtureClass}")
        }

        val (factory, fixtureClass) = fixtureFactories
            .filter { it.isCompatible(testData) }
            .filterIsInstance<FixtureFactory<T>>()
            .map { factory -> factory to fixtureClasses.firstOrNull { factory.match(it, testData) } }
            .firstOrNull { it.second != null } ?: throw IllegalArgumentException(
            "No matching Fixture found for TestData of type: " +
                    testData::class.qualifiedName + " available Fixtures: " + fixtureClasses
        )
        fixtureClass ?: throw IllegalStateException("this is not possible")

        val fixtureContextImpl = FixtureContextImpl(
            fixtureClass,
            extensionContext
        )
        val internalContext = createContext(fixtureClass, context, fixtureContextImpl)
        return factory.getFixture(fixtureContextImpl, FixtureExtensionsManager(extensionManager, internalContext))
    }

    /**
     * Create the internal EngineContext required by the ExtensionManager. This context is not exposed to the Fixture
     * implementation.
     */
    private fun createContext(
        fixtureClass: KClass<*>,
        parent: EngineContext,
        extensionContext: ExtensionContext
    ): EngineContext {
        val extensionRegistry =
            ExtensionRegistryImpl.createRegistryFrom(loadExtensions(fixtureClass), parent.extensionRegistry)
        return EngineContext(parent, extensionContext, extensionRegistry)
    }
}

val DocumentFixtureContext.externalFixtureClasses: List<KClass<*>>
    get() = this.documentFixtureClass.findAnnotation<ExecutableDocument>()!!.fixtureClasses.toList()

val DocumentFixtureContext.internalFixtureClasses: List<KClass<*>>
    get() = this.documentFixtureClass.nestedClasses.filter { it.isFixtureClass() }

fun KClass<*>.isFixtureClass(): Boolean {
    return this.annotations.any { annotation -> annotation.isFixtureAnnotation() }
}

fun Annotation.isFixtureAnnotation(): Boolean {
    return this.annotationClass.hasAnnotation<FixtureAnnotation>()
}
