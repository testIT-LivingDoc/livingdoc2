package org.livingdoc.converters

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.javaMethod

internal class JSONConverterTest {

    val cut = JSONConverter<CustomType>()

    @Test
    fun `converter can converted to Custom Type`() {
        val result = cut.convert("""{"text":"bla","number":17}""",
            FakeFixture::fakeMethod.javaMethod?.parameters?.get(0), null)
        Assertions.assertThat(result.number).isEqualTo(17)
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
