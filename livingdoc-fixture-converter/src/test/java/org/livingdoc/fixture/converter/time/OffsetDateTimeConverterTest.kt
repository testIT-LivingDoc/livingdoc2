package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.TypeConverter
import java.time.OffsetDateTime
import java.time.OffsetDateTime.parse


internal class OffsetDateTimeConverterTest : TemporalConverterContract<OffsetDateTime>() {

    val cut = OffsetDateTimeConverter()

    override fun getCut(): TypeConverter<OffsetDateTime> = cut

    override fun getValidInputVariations() = mapOf(
            "2017-05-12T12:34+01:00" to parse("2017-05-12T12:34+01:00"),
            "2017-05-12T12:34:56+01:00" to parse("2017-05-12T12:34:56+01:00"),
            "2017-05-12T12:34:56.123+01:00" to parse("2017-05-12T12:34:56.123+01:00")
    )

    override fun getDefaultFormatValue() = "2017-05-12T12:34+01:00" to parse("2017-05-12T12:34+01:00")

    override fun getCustomFormat() = "dd.MM.uuuu HH:mm 'Uhr' X"
    override fun getCustomFormatValue() = "12.05.2017 12:34 Uhr +01" to parse("2017-05-12T12:34+01:00")
    override fun getMalformedCustomFormat() = "dd.MM.uuuu HH:mm X V"

}
