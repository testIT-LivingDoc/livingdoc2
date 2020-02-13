package org.livingdoc.jvm.api.fixture

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.repositories.model.TestData
import kotlin.reflect.KClass

interface FixtureFactory<T : TestData> {
    fun isCompatible(testData: TestData): Boolean

    fun match(fixtureClass: KClass<*>, testData: T): Boolean

    fun getFixture(context: FixtureContext): Fixture<T>
}
