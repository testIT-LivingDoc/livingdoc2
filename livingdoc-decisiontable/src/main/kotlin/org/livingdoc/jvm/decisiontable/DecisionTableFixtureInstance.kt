package org.livingdoc.jvm.decisiontable

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class DecisionTableFixtureInstance(var instance: Any) {
    companion object {
        fun createInstance(fixtureClass: KClass<*>): DecisionTableFixtureInstance {
            return DecisionTableFixtureInstance(fixtureClass.createInstance())
        }
    }
}
