package org.livingdoc.converters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConvertersTestFixtures.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

internal class TypeConvertersTest {

    @Nested
    inner class parameters {

        @Test
        fun `annotated parameter`() {
            val typeConverter = getParameterTypeConverter(contextOf(AnnotatedMethodParameter::class))
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated method`() {
            val typeConverter = getParameterTypeConverter(contextOf(AnnotatedMethod::class))
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated class`() {
            val typeConverter = getParameterTypeConverter(contextOf(AnnotatedClass::class))
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `default fallback without document`() {
            val typeConverter = getParameterTypeConverter(contextOf(NoAnnotations::class))
            assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
        }

        @Nested
        inner class `with document` {

            @Test
            fun `annotated document class`() {
                val typeConverter =
                    getParameterTypeConverter(contextOf(DocumentWithAnnotation::class, NoAnnotations::class))
                assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
            }

            @Test
            fun `default fallback when no document annotation`() {
                val typeConverter =
                    getParameterTypeConverter(contextOf(DocumentWithoutAnnotation::class, NoAnnotations::class))
                assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
            }
        }

        /**
         * Use the first parameter of the function with the name method on the context element. [context] element must
         * be a KClass.
         */
        private fun getParameterTypeConverter(context: Context): TypeConverter<*>? {
            val fixtureClass = context.element as KClass<*>
            val function = fixtureClass.memberFunctions.first { it.name == "method" }
            val parameter = function.valueParameters[0]
            val extendedContext = context.createContext(function).createContext(parameter)

            return TypeConverters.findTypeConverter("", parameter.type, extendedContext)
        }
    }

    @Nested
    inner class fields {

        @Test
        fun `annotated field`() {
            val typeConverter = getFieldTypeConverter(contextOf(AnnotatedField::class))
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `annotated class`() {
            val typeConverter = getFieldTypeConverter(contextOf(AnnotatedClass::class))
            assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
        }

        @Test
        fun `default fallback without document`() {
            val typeConverter = getFieldTypeConverter(contextOf(NoAnnotations::class))
            assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
        }

        @Nested
        inner class `with document` {

            @Test
            fun `annotated document class`() {
                val typeConverter =
                    getFieldTypeConverter(contextOf(DocumentWithAnnotation::class, NoAnnotations::class))
                assertThat(typeConverter).isInstanceOf(CustomBooleanConverter::class.java)
            }

            @Test
            fun `default fallback when no document annotation`() {
                val typeConverter =
                    getFieldTypeConverter(contextOf(DocumentWithoutAnnotation::class, NoAnnotations::class))
                assertThat(typeConverter).isInstanceOf(BooleanConverter::class.java)
            }
        }

        /**
         * Use the property named field of the context element. [context] have a KClass as element.
         */
        private fun getFieldTypeConverter(context: Context): TypeConverter<*>? {
            val fixtureClass = context.element as KClass<*>
            val field = fixtureClass.memberProperties.first { it.name == "field" }
            val extendedContext = context.createContext(field)
            return TypeConverters.findTypeConverter("", field.returnType, extendedContext)
        }
    }
}
