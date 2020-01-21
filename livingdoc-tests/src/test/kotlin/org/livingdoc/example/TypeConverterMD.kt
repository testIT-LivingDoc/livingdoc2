package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.converters.JSONConverter
import org.livingdoc.converters.color.ColorConverter

@ExecutableDocument("local://TypeConverter.md")
class TypeConverterMD {

    @ScenarioFixture
    class ScenarioTests {

        @Step("CustomType {json} has property values {string} and {number}")
        fun testCustomTypeJson(
            @Binding("json") @Converter(JSONConverter::class) json: CustomType,
            @Binding("string") string: String,
            @Binding("number") number: Int
        ) {
            assertThat(json.text).isEqualTo(string)
            assertThat(json.number).isEqualTo(number)
        }

        @Step("The ColorConverter convert {color} to {hex}")
        fun testColorConverter(
            @Binding("color") @Converter(ColorConverter::class) color: String,
            @Binding("hex") hex: String
        ) {
            assertThat(color).isEqualTo(hex)
        }
    }
}
