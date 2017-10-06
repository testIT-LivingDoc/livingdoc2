package org.livingdoc.converters.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

internal abstract class CollectionWithSingleParamContract<T : Collection<Any>> : CollectionConverterContract() {

    override abstract val cut: AbstractCollectionWithSingleParamConverter<T>
    abstract val intExpectation: Collection<Int>
    abstract val booleanExpectation: Collection<Boolean>

    @Test
    fun `canConvertBoolean`() {
        val input = "true, false, false, true"
        val annotatedElement = fakeBooleanMethodParam()

        val converted = cut.convert(input, annotatedElement, null)
        assertThat(converted).isEqualTo(booleanExpectation)
    }

    @Test
    fun `canConvertInt`() {
        val input = "1, 2, 3, 4"
        val parameterTypeConverter = fixtureClass.memberProperties.first { it.name == "integer" }.javaField

        val converted = cut.convert(input, parameterTypeConverter as Field, null)
        assertThat(converted).isEqualTo(intExpectation)
    }
}
