package org.livingdoc.engine.fixtures

import fixtures.TypeConvertersTestFixtures.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.BooleanConverter
import kotlin.reflect.KClass


internal class TypeConvertersTest {

    val booleanClass = java.lang.Boolean::class.java

    @Nested
    inner class `parameters` {

        @Test
        fun `annotated parameter`() {
            val typeConverter = getParameterTypeConverter(AnnotatedMethodParameter::class)
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated method`() {
            val typeConverter = getParameterTypeConverter(AnnotatedMethod::class)
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated class`() {
            val typeConverter = getParameterTypeConverter(AnnotatedClass::class)
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `default fallback without document`() {
            val typeConverter = getParameterTypeConverter(NoAnnotations::class)
            assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
        }

        @Nested
        inner class `with document` {

            @Test
            fun `annotated document class`() {
                val typeConverter = getParameterTypeConverter(NoAnnotations::class, DocumentWithAnnotation::class)
                assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
            }

            @Test
            fun `default fallback when no document annotation`() {
                val typeConverter = getParameterTypeConverter(NoAnnotations::class, DocumentWithoutAnnotation::class)
                assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
            }

        }

        private fun getParameterTypeConverter(fixtureClass: KClass<*>, documentClass: KClass<*>? = null): TypeConverter<*>? {
            val method = fixtureClass.java.getMethod("method", booleanClass)
            val parameter = method.parameters[0]
            return TypeConverters.findTypeConverter(parameter, documentClass?.java)
        }

    }

    @Nested
    inner class `fields` {

        @Test
        fun `annotated field`() {
            val typeConverter = getFieldTypeConverter(AnnotatedField::class)
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated class`() {
            val typeConverter = getFieldTypeConverter(AnnotatedClass::class)
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `default fallback without document`() {
            val typeConverter = getFieldTypeConverter(NoAnnotations::class)
            assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
        }

        @Nested
        inner class `with document` {

            @Test
            fun `annotated document class`() {
                val typeConverter = getFieldTypeConverter(NoAnnotations::class, DocumentWithAnnotation::class)
                assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
            }

            @Test
            fun `default fallback when no document annotation`() {
                val typeConverter = getFieldTypeConverter(NoAnnotations::class, DocumentWithoutAnnotation::class)
                assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
            }

        }

        private fun getFieldTypeConverter(fixtureClass: KClass<*>, documentClass: KClass<*>? = null): TypeConverter<*>? {
            val field = fixtureClass.java.getField("field")
            return TypeConverters.findTypeConverter(field, documentClass?.java)
        }

    }

}