package org.livingdoc.engine.fixtures

import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters
import java.lang.reflect.Field

class FixtureFieldInjector(
        private val document: Any?
) {

    /**
     * Injects the given `field` of the given `fixture` instance with the given [String] `value`.
     *
     * Before injecting the field, the [String] value will be converted into an object of the appropriate type.
     * This is done by looking up a matching [TypeConverter] as follows:
     *
     * 1. `field`
     * 2. `class`
     * 3. `document`
     * 4. `default`
     *
     * This method is capable of injecting any field of the given fixture instance. If the field is not
     * public, it will be made so.
     *
     * @param field the [Field] to inject
     * @param fixture the fixture instance to invoke the method on
     * @param value the value for the injected field as strings
     * @throws FixtureFieldInjectionException in case anything went wrong with the injection
     */
    fun inject(field: Field, fixture: Any, value: String) {
        try {
            doInject(field, fixture, value)
        } catch (e: Exception) {
            throw FixtureFieldInjectionException(field, fixture, e)
        }
    }

    private fun doInject(field: Field, fixture: Any, value: String) {
        val convertedValue = convert(value, field)
        with(field) {
            isAccessible = true
            set(fixture, convertedValue)
        }
    }

    private fun convert(value: String, field: Field): Any {
        val documentClass = document?.javaClass
        val typeConverter = TypeConverters.findTypeConverter(field, documentClass)
                ?: throw NoTypeConverterFoundException(field)
        return typeConverter.convert(value, field, documentClass)
    }

    class FixtureFieldInjectionException(field: Field, fixture: Any, e: Exception)
        : RuntimeException("Could not inject field '$field' into fixture '$fixture' because of an exception:", e)

    internal class NoTypeConverterFoundException(field: Field)
        : RuntimeException("No type converter could be found to convert field: $field")

}
