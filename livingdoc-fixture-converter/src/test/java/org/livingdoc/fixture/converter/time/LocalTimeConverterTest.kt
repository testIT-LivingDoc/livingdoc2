package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.TypeConverter
import java.time.LocalTime
import java.time.LocalTime.parse


internal class LocalTimeConverterTest : TemporalConverterContract<LocalTime>() {

    val cut = LocalTimeConverter()

    override fun getCut(): TypeConverter<LocalTime> = cut

    override fun getValidInputVariations() = mapOf(
            "12:34" to parse("12:34"),
            "12:34:56" to parse("12:34:56")
    )

    override fun getDefaultFormatValue() = "12:34" to parse("12:34")

    override fun getCustomFormat() = "HH:mm 'Uhr'"
    override fun getCustomFormatValue() = "12:34 Uhr" to parse("12:34:00")
    override fun getMalformedCustomFormat() = "HH:mm V"

}
