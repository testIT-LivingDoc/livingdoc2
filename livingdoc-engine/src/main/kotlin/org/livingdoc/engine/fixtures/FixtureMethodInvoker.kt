package org.livingdoc.engine.fixtures

import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class FixtureMethodInvoker(
        private val document: Any?
) {

    /**
     * Invoke the given `method` on the given `fixture` instance with the given [String] `arguments`.
     *
     * Before invoking the method, the [String] arguments will be converted into objects of the appropriate types.
     * This is done by looking up a matching [TypeConverter] as follows:
     *
     * 1. `parameter`
     * 2. `method`
     * 3. `class`
     * 4. `document`
     * 5. `default`
     *
     * This method is capable of invoking any method on the given fixture instance. If the invoked method is not
     * public, it will be made so.
     *
     * @param method the [Method] to invoke
     * @param fixture the fixture instance to invoke the method on
     * @param arguments the arguments for the invoked method as strings
     * @return the result of the invocation or `null` in case the invoked method has not return type (`void` / `Unit`)
     * @throws FixtureMethodInvocationException in case anything went wrong with the invocation
     */
    fun invoke(method: Method, fixture: Any, arguments: Array<String> = emptyArray()): Any? {
        try {
            return doInvoke(method, fixture, arguments)
        } catch (e: RuntimeException) {
            throw FixtureMethodInvocationException(method, fixture, e)
        }
    }

    private fun doInvoke(method: Method, fixture: Any, arguments: Array<String>): Any? {
        val methodParameters = method.parameters
        assertThatAllArgumentsForMethodAreProvided(arguments, methodParameters)
        val convertedArguments = convert(arguments, methodParameters)
        return with(method) {
            isAccessible = true
            invoke(fixture, *convertedArguments)
        }
    }

    private fun assertThatAllArgumentsForMethodAreProvided(arguments: Array<String>, methodParameters: Array<Parameter>) {
        val numberOfArguments = arguments.size
        val numberOfMethodParameters = methodParameters.size
        if (numberOfArguments != numberOfMethodParameters) {
            throw MismatchingNumberOfArgumentsException(numberOfArguments, numberOfMethodParameters)
        }
    }

    private fun convert(arguments: Array<String>, methodParameters: Array<Parameter>): Array<Any> {
        val convertedArguments = mutableListOf<Any>()
        for (i in arguments.indices) {
            val argument = arguments[i]
            val methodParameter = methodParameters[i]
            val convertedArgument = convert(argument, methodParameter)
            convertedArguments.add(convertedArgument)
        }
        return convertedArguments.toTypedArray()
    }

    private fun convert(argument: String, methodParameter: Parameter): Any {
        val documentClass = document?.javaClass
        val typeConverter = TypeConverters.findTypeConverter(methodParameter, documentClass)
        if (typeConverter != null) {
            return typeConverter.convert(argument, methodParameter)
        }
        throw NoTypeConverterFoundException(methodParameter)
    }

    class FixtureMethodInvocationException(method: Method, fixture: Any, e: RuntimeException)
        : RuntimeException("Could not invoke method '$method' on fixture '$fixture' because of an exception:", e)

    internal class MismatchingNumberOfArgumentsException(args: Int, params: Int)
        : RuntimeException("Method argument number mismatch: arguments = $args, method parameters = $params")

    internal class NoTypeConverterFoundException(parameter: Parameter)
        : RuntimeException("No type converter could be found to convert method parameter: $parameter")

}
