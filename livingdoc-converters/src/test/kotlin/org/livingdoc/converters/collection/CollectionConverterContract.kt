package org.livingdoc.converters.collection

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

internal abstract class CollectionConverterContract : DefaultTypeConverterContract {

    abstract val collectionClass: Class<*>
    abstract val fixtureClass: KClass<*>

    @Test
    fun `converter can be converted to kotlin collection`() {
        assertThat(cut.canConvertTo(collectionClass)).isTrue()
    }

    @Test
    fun `non field or parameter is not viable to be converted`() {
        val annotatedElement = fakeMethodParam("noType")
        Assertions.assertThrows(AbstractCollectionConverter.NoTypeConverterFoundException::class.java) {
            cut.convert("no typeconverter for type", annotatedElement, null)
        }
    }

    @Test
    fun `no viable typeConverter found`() {
        val element: AnnotatedElement = mock()

        Assertions.assertThrows(IllegalStateException::class.java) {
            cut.convert("not a viable annotated element", element, null)
        }
    }

    internal fun fakeBooleanMethodParam(): Parameter {
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
