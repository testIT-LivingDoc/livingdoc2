package org.livingdoc.jvm.scenario

import org.livingdoc.converters.TypeConverters
import org.livingdoc.converters.contextOf
import org.livingdoc.converters.createContext
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
        val context = contextOf(fixture.instance::class) // TODO
        val convertedArguments = args.map { (key, value) ->
            // TODO what if type is error?
            // TODO handle it here or in converter?
            key to TypeConverters.convertStringToType(value, key.type, context.createContext(key))
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
