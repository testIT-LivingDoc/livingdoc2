package org.livingdoc.converters.collection

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.Context
import org.livingdoc.converters.DefaultTypeConverterContract
import org.livingdoc.converters.TypeConverters
import org.livingdoc.converters.contextWithAnnotation
import org.livingdoc.converters.convertValueForParameter
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

internal abstract class CollectionConverterContract : DefaultTypeConverterContract {

    abstract val intExpectation: Any
    abstract val booleanExpectation: Any
    abstract val booleanInput: String
    abstract val intInput: String
    abstract val collectionClass: KClass<*>
    abstract val fixtureClass: KClass<*>

    @Test
    fun `can convert boolean`() {
        val annotatedElement = fakeBooleanMethodParam()

        val converted = cut.convert(booleanInput, annotatedElement.type, Context(annotatedElement, null))
        assertThat(converted).isEqualTo(booleanExpectation)
    }

    @Test
    fun `can convert int`() {
        val property = fakeIntegerField()

        val converted = cut.convert(intInput, property.returnType, Context(property, null))
        assertThat(converted).isEqualTo(intExpectation)
    }

    @Test
    fun `converter can be converted to kotlin collection`() {
        assertThat(cut.canConvertTo(collectionClass)).isTrue()
    }

    @Test
    fun `non field or parameter is not viable to be converted`() {
        val parameter = fakeMethodParam("noType")
        assertThrows(TypeConverters.NoTypeConverterFoundException::class.java) {
            cut.convertValueForParameter("no typeconverter for type, value", parameter)
        }
    }

    @Test
    fun `no viable typeConverter found`() {
        val type: KType = mockk()
        every { type.arguments } returns emptyList()
        assertThrows(IllegalStateException::class.java) {
            cut.convert("not a viable annotated element", type, contextWithAnnotation(emptyList()))
        }
    }

    private fun fakeBooleanMethodParam(): KParameter {
        return fakeMethodParam("boolean")
    }

    private fun fakeMethodParam(methodName: String): KParameter {
        val function = fixtureClass.functions.first { it.name == methodName }
        return function.valueParameters[0]
    }

    internal fun fakeIntegerField(): KProperty<*> {
        return fixtureClass.memberProperties.first { it.name == "integer" }
    }
}
