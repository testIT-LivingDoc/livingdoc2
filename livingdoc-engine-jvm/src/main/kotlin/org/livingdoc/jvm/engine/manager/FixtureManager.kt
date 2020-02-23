package org.livingdoc.jvm.engine.manager

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureAnnotation
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.jvm.engine.EngineContext
import org.livingdoc.jvm.engine.extension.FixtureContextImpl
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

        return fixtureFactories.filter { it.isCompatible(testData) }.filterIsInstance<FixtureFactory<T>>()
            .map { factory ->
                val fixtureClass = fixtureClasses.firstOrNull { factory.match(it, testData) }
                fixtureClass?.let {
                    val fixtureContextImpl = FixtureContextImpl(fixtureClass, extensionContext)
                    val internalContext = createContext(it, context, fixtureContextImpl)
                    factory.getFixture(fixtureContextImpl, FixtureExtensionsManager(extensionManager, internalContext))
                }
            }.firstOrNull()
            ?: throw IllegalArgumentException(
                "No matching Fixture found for TestData of type: " +
                        testData::class.qualifiedName + " available Fixtures: " + fixtureClasses
            )
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
        return EngineContext(parent, extensionContext, loadExtensions(fixtureClass))
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
