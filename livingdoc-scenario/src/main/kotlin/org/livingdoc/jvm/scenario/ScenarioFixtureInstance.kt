package org.livingdoc.jvm.scenario

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class ScenarioFixtureInstance(val instance: Any) {
    companion object {
        /**
         * Creates a new instance of the fixture class passed to this execution
         */
        fun createFixtureInstance(fixtureClass: KClass<*>): ScenarioFixtureInstance {
            return ScenarioFixtureInstance(fixtureClass.createInstance())
        }
    }
}
