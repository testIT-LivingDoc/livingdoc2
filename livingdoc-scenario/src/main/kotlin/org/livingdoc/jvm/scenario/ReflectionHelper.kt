package org.livingdoc.jvm.scenario

import org.livingdoc.api.conversion.Context
import org.livingdoc.converters.TypeConverters
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.instanceParameter

object ReflectionHelper {
    /**
     * invokes function with parameter but without return value.
     * this function does not handle any exceptions
     */
    fun invokeWithParameterWithoutReturnValue(
        function: KFunction<*>,
        fixture: ScenarioFixtureInstance,
        args: Map<KParameter, String>
    ) {

        val convertedArguments = args.map { (key, value) ->
            key to TypeConverters.convertStringToType(value, key.type, Context(key, null))
        }.toMap()

        val instanceParameter = function.instanceParameter ?: error("function must be instance member")

        function.callBy(convertedArguments + mapOf(instanceParameter to fixture.instance))
    }

    /**
     * invokes function without parameter and without return value.
     * this function does not handle any exceptions
     */
    fun invokeWithoutParameterWithoutReturnValue(
        function: KFunction<*>,
        fixture: ScenarioFixtureInstance
    ) {

        val instanceParameter = function.instanceParameter ?: error("function must be instance member")

        function.callBy(mapOf(instanceParameter to fixture.instance))
    }
}
