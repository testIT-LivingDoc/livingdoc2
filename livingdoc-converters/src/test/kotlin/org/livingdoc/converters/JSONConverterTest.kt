package org.livingdoc.converters

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.api.conversion.ConversionException
import kotlin.reflect.jvm.javaMethod

internal class JSONConverterTest {

    val cut = JSONConverter<CustomType>()

    @Test
    fun `converter can converted to Custom Type`() {
        val result = cut.convert(
            """{"text":"bla","number":17}""",
            FakeFixture::fakeMethod.javaMethod?.parameters?.get(0), null
        )
        Assertions.assertThat(result.number).isEqualTo(17)
    }

    @ParameterizedTest
    @ValueSource(strings = ["[]", """"some text""""])
    fun `throw exception if input is not json object`(json: String) {
        assertThrows(ConversionException::class.java) {
            cut.convert(json, FakeFixture::fakeMethod.javaMethod?.parameters?.get(0), null)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["""{"text:"bla","number":17}""", "{", "", "15"])
    fun `throw exception on invalid json input`(json: String) {
        assertThrows(ConversionException::class.java) {
            cut.convert(json, FakeFixture::fakeMethod.javaMethod?.parameters?.get(0), null)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["""{"text":42,"number":17}""", """{"number":17}""", "{}"])
    fun `throw exception if json not match type`(json: String) {
        assertThrows(ConversionException::class.java) {
            cut.convert(json, FakeFixture::fakeMethod.javaMethod?.parameters?.get(0), null)
        }
    }

    data class CustomType(
        val text: String,
        val number: Int
    )

    class FakeFixture {
        fun fakeMethod(param: CustomType) {
        }
    }
}
