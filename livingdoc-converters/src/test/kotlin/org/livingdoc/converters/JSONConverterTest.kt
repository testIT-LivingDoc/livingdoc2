package org.livingdoc.converters

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.api.conversion.ConversionException
import kotlin.reflect.full.valueParameters

internal class JSONConverterTest {

    val cut = JSONConverter<CustomType>()

    @Test
    fun `converter can converted to Custom Type`() {
        val result = cut.convertValueForParameter(
            """{"text":"bla","number":17}""",
            FakeFixture::fakeMethod.valueParameters[0]
        )
        Assertions.assertThat(result.number).isEqualTo(17)
    }

    @ParameterizedTest
    @ValueSource(strings = ["[]", """"some text""""])
    fun `throw exception if input is not json object`(json: String) {
        val parameter = FakeFixture::fakeMethod.valueParameters[0]
        assertThrows(ConversionException::class.java) {
            cut.convertValueForParameter(json, parameter)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["""{"text:"bla","number":17}""", "{", "", "15"])
    fun `throw exception on invalid json input`(json: String) {
        assertThrows(ConversionException::class.java) {
            cut.convertValueForParameter(json, FakeFixture::fakeMethod.valueParameters[0])
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["""{"text":42,"number":17}""", """{"number":17}""", "{}"])
    fun `throw exception if json not match type`(json: String) {
        assertThrows(ConversionException::class.java) {
            cut.convertValueForParameter(json, FakeFixture::fakeMethod.valueParameters[0])
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
