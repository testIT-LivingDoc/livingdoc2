package org.livingdoc.jvm.api.fixture

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.repositories.model.TestData
import kotlin.reflect.KClass

interface FixtureFactory<T : TestData> {
    fun isCompatible(testData: TestData): Boolean

    /**
     * Check if the given [fixtureClass] can be handles by this Factory and matches the [testData]. If the fixtureClass does not
     * correspond to this factory, false should be returned.
     * Should throw an exception if the fixtureClass correspond to this Factory but does not have a valid format.
     */
    fun match(fixtureClass: KClass<*>, testData: T): Boolean

    fun getFixture(context: FixtureContext, manager: FixtureExtensionsInterface): Fixture<T>
}
