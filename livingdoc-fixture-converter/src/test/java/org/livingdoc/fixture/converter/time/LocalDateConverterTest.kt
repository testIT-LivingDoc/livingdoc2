package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.TypeConverter
import java.time.LocalDate
import java.time.LocalDate.parse


internal class LocalDateConverterTest : TemporalConverterContract<LocalDate>() {

    val cut = LocalDateConverter()

    override fun getCut(): TypeConverter<LocalDate> = cut

    override fun getValidInputVariations() = mapOf(
            "2017-05-12" to parse("2017-05-12")
    )

    override fun getDefaultFormatValue() = "2017-05-12" to parse("2017-05-12")

    override fun getCustomFormat() = "dd.MM.uuuu"
    override fun getCustomFormatValue() = "12.05.2017" to parse("2017-05-12")
    override fun getMalformedCustomFormat() = "dd.MM.uuuu V"

}
