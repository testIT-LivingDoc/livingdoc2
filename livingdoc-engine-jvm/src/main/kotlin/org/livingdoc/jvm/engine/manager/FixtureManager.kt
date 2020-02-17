package org.livingdoc.jvm.engine.manager

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
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
    fun <T : TestData> getFixture(context: EngineContext, testData: T): Fixture<T> {
        val extensionContext = context.extensionContext as DocumentFixtureContext
        val fixtureClasses = extensionContext.externalFixtureClasses + extensionContext.internalFixtureClasses
        return fixtureFactories.filter { it.isCompatible(testData) }.filterIsInstance<FixtureFactory<T>>()
            .map { factory ->
                val fixtureClass = fixtureClasses.firstOrNull { factory.match(it, testData) }
                fixtureClass?.let {
                    val fixtureContextImpl = FixtureContextImpl(fixtureClass, extensionContext)
                    factory.getFixture(fixtureContextImpl)
                }
            }.firstOrNull() ?: throw IllegalArgumentException("No matching Fixture found")
    }
}

val DocumentFixtureContext.externalFixtureClasses: List<KClass<*>>
    get() = this.documentFixtureClass.findAnnotation<ExecutableDocument>()!!.fixtureClasses.toList()

val DocumentFixtureContext.internalFixtureClasses: List<KClass<*>>
    get() = this.documentFixtureClass.nestedClasses.filter {
        it.annotations.any { it.annotationClass.hasAnnotation<FixtureAnnotation>() }
    }
