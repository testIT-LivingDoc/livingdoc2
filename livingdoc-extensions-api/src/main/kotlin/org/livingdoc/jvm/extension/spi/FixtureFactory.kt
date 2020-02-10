package org.livingdoc.jvm.extension.spi

import org.livingdoc.jvm.extension.Fixture
import org.livingdoc.jvm.extension.FixtureContext
import org.livingdoc.repositories.model.TestData
import kotlin.reflect.KClass

interface FixtureFactory<T : TestData> {
    fun isCompatible(testData: TestData): Boolean

    fun match(fixtureClass: KClass<*>, testData: T): Boolean

    fun getFixture(context: FixtureContext): Fixture<T>
}
