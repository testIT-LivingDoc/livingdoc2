package org.livingdoc.converters.collection

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import org.livingdoc.converters.TypeConverters
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

internal abstract class CollectionConverterContract : DefaultTypeConverterContract {

    abstract val intExpectation: Any
    abstract val booleanExpectation: Any
    abstract val booleanInput: String
    abstract val intInput: String
    abstract val collectionClass: Class<*>
    abstract val fixtureClass: KClass<*>

    @Test
    fun `can convert boolean`() {
        val annotatedElement = fakeBooleanMethodParam()

        val converted = cut.convert(booleanInput, annotatedElement, null)
        assertThat(converted).isEqualTo(booleanExpectation)
    }

    @Test
    fun `can convert int`() {
        val annotatedElement = fakeIntegerField()

        val converted = cut.convert(intInput, annotatedElement, null)
        assertThat(converted).isEqualTo(intExpectation)
    }

    @Test
    fun `converter can be converted to kotlin collection`() {
        assertThat(cut.canConvertTo(collectionClass)).isTrue()
    }

    @Test
    fun `non field or parameter is not viable to be converted`() {
        val annotatedElement = fakeMethodParam("noType")
        assertThrows(TypeConverters.NoTypeConverterFoundException::class.java) {
            cut.convert("no typeconverter for type", annotatedElement, null)
        }
    }

    @Test
    fun `no viable typeConverter found`() {
        val element: AnnotatedElement = mockk()

        assertThrows(IllegalStateException::class.java) {
            cut.convert("not a viable annotated element", element, null)
        }
    }

    private fun fakeBooleanMethodParam(): Parameter {
        return fakeMethodParam("boolean")
    }

    private fun fakeMethodParam(methodName: String): Parameter {
        val method = fixtureClass.functions.first { it.name == methodName }.javaMethod
        return method!!.parameters[0]!!
    }

    internal fun fakeIntegerField(): Field {
        return fixtureClass.memberProperties.first { it.name == "integer" }.javaField!!
    }
}
