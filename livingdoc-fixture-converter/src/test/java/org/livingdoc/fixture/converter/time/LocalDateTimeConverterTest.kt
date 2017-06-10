package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.TypeConverter
import java.time.LocalDateTime
import java.time.LocalDateTime.parse


internal class LocalDateTimeConverterTest : TemporalConverterContract<LocalDateTime>() {

    val cut = LocalDateTimeConverter()

    override fun getCut(): TypeConverter<LocalDateTime> = cut

    override fun getValidInputVariations() = mapOf(
            "2017-05-12T12:34" to parse("2017-05-12T12:34"),
            "2017-05-12T12:34:56" to parse("2017-05-12T12:34:56"),
            "2017-05-12T12:34:56.123" to parse("2017-05-12T12:34:56.123")
    )

    override fun getDefaultFormatValue() = "2017-05-12T12:34" to parse("2017-05-12T12:34")

    override fun getCustomFormat() = "dd.MM.uuuu HH:mm 'Uhr'"
    override fun getCustomFormatValue() = "12.05.2017 12:34 Uhr" to parse("2017-05-12T12:34")
    override fun getMalformedCustomFormat() = "dd.MM.uuuu HH:mm V"

}
