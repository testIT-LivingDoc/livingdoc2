package org.livingdoc.converters.collection

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

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
        val parameterTypeConverter = fixtureClass.memberProperties.first { it.name == "integer" }.javaField

        val converted = cut.convert(input, parameterTypeConverter as Field, null)
        assertThat(converted).isEqualTo(intExpectation)
    }

    @Test
    fun `converter can converted to Kotlin List`() {
        assertThat(cut.canConvertTo(collectionClass)).isTrue()
    }

    @Test
    fun `non field or parameter is not viable to be converted`() {
        Assertions.assertThrows(AbstractCollectionConverter.NoTypeConverterFoundException::class.java) {
            runConvert("no typeconverter for type", "noType")
        }
    }

    @Test
    fun `non viable typeConverter found`() {
        val element: AnnotatedElement = mock()

        Assertions.assertThrows(IllegalStateException::class.java) {
            cut.convert("not a viable annotated element", element, null)
        }
    }

    private fun runConvert(input: String, methodName: String): T {
        val parameterTypeConverter = getParameterTypeConverter(fixtureClass, methodName)
        return cut.convert(input, parameterTypeConverter, null)
    }

    private fun getParameterTypeConverter(fixtureClass: KClass<*>, methodName: String): AnnotatedElement {
        val method = fixtureClass.memberFunctions.first { it.name == methodName }.javaMethod
        return method!!.parameters[0]
    }
}
