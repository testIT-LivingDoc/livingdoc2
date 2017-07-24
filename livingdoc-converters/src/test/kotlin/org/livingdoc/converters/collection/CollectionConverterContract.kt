package org.livingdoc.converters.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.AnnotatedElement
import kotlin.reflect.KClass

internal abstract class CollectionConverterContract<T : Collection<Any>> {

    abstract val cut: AbstractCollectionConverter<T>
    abstract val collectionClass: Class<*>
    abstract val fixtureClass: KClass<*>
    abstract val intExpectation: Collection<Any>
    abstract val booleanExpectation: Collection<Any>

    @Test
    fun `canConvertBoolean`() {
        val input = "true, false, false, true"
        val converted = runConvert(input, "boolean")

        assertThat(converted).isEqualTo(booleanExpectation)
    }

    @Test
    fun `canConvertInt`() {
        val input = "1, 2, 3, 4"
        val converted = runConvert(input, "integer")
        assertThat(converted).isEqualTo(intExpectation)
    }

    @Test
    fun `converter can converted to Kotlin List`() {
        assertThat(cut.canConvertTo(collectionClass)).isTrue()
    }

    private fun runConvert(input: String, methodName: String): T {
        val parameterTypeConverter = getParameterTypeConverter(fixtureClass, methodName)
        return cut.convert(input, parameterTypeConverter, null)
    }

    private fun getParameterTypeConverter(fixtureClass: KClass<*>, methodName: String): AnnotatedElement? {
        val method = fixtureClass.java.getMethod(methodName, collectionClass)
        return method.parameters[0]
    }

}
