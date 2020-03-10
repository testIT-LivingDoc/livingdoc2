package org.livingdoc.jvm.api.fixture

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.repositories.model.TestData
import kotlin.reflect.KClass

/**
 * A FixtureFactory is used to dynamically match and create [Fixture Handlers][Fixture] for provided TestData from a
 * Business Expert and a set of Fixtures from a Test Engineer. This Interface must be implemented if a new Fixture type
 * should be added to LivingDoc. Factory implementations are loaded via Java SPI, so don't forget to add it to the your
 * implementation to the Service descriptor file `META-INF/services/org.livingdoc.jvm.api.fixture.FixtureFactory`.
 *
 * All Fixtures of a Test Engineer must be Annotated with an Fixture annotation. Fixture annotations are annotations,
 * which are meta-annotated with [FixtureAnnotation].
 */
interface FixtureFactory<T : TestData> {
    /**
     * Check if the FixtureFactory can handle the type of [testData].
     */
    fun isCompatible(testData: TestData): Boolean

    /**
     * Check if the given [fixtureClass] can be handles by this Factory and matches the [testData]. If the fixtureClass
     * does not correspond to this factory, false should be returned.
     * Should throw an exception if the fixtureClass correspond to this Factory but does not have a valid format.
     */
    fun match(fixtureClass: KClass<*>, testData: T): Boolean

    /**
     * Create a new Fixture instance for the given [context] and [Fixture Manager][manager]. This Function should not
     * throw any Exceptions. The [FixtureManager] must be used to call extension callbacks.
     */
    fun getFixture(context: FixtureContext, manager: FixtureManager): Fixture<T>
}
