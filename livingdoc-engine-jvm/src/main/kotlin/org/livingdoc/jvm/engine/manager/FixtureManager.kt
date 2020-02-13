package org.livingdoc.jvm.engine.manager

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.engine.extension.FixtureContextImpl
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureAnnotation
import org.livingdoc.jvm.api.fixture.FixtureFactory
import org.livingdoc.repositories.model.TestData
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class FixtureManager {
    private val fixtureFactories: List<FixtureFactory<*>> =
        ServiceLoader.load(FixtureFactory::class.java).iterator().asSequence().toList()

    fun <T : TestData> getFixture(context: DocumentFixtureContext, testData: T): Fixture<T> {
        val fixtureClasses = context.externalFixtureClasses + context.internalFixtureClasses
        return fixtureFactories.filter { it.isCompatible(testData) }.filterIsInstance<FixtureFactory<T>>()
            .map { factory ->
                val fixtureClass = fixtureClasses.firstOrNull { factory.match(it, testData) }
                fixtureClass?.let {
                    val fixtureContextImpl = FixtureContextImpl(fixtureClass, context)
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
