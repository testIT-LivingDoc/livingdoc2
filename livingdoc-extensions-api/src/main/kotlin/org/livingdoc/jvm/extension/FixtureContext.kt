package org.livingdoc.jvm.extension

import org.livingdoc.engine.Fixture
import org.livingdoc.repositories.model.TestData
import kotlin.reflect.KClass

interface FixtureContext<out T: TestData> {
    val fixtureClass: KClass<*>

    val fixture: Fixture<*>

    /**
     * A test data instance that can be executed by the fixture
     */
    val testData: T

    val documentFixtureContext: DocumentFixtureContext
}
