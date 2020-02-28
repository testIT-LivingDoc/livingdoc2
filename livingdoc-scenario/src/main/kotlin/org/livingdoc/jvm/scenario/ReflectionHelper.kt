package org.livingdoc.jvm.scenario

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.instanceParameter

object ReflectionHelper {
    fun invokeWithParameterWithoutReturnValue(function: KFunction<*>, fixture: ScenarioFixtureInstance, args: Map<KParameter, String>) {
        val instanceParameter = function.instanceParameter ?: error("function must be instance member")

        function.callBy(args + mapOf(instanceParameter to fixture.instance))
    }
}
