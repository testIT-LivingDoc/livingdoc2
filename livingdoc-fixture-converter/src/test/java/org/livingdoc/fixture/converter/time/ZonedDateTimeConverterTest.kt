package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.TypeConverter
import java.time.ZonedDateTime
import java.time.ZonedDateTime.parse


internal class ZonedDateTimeConverterTest : TemporalConverterContract<ZonedDateTime>() {

    val cut = ZonedDateTimeConverter()

    override fun getCut(): TypeConverter<ZonedDateTime> = cut

    override fun getValidInputVariations() = mapOf(
            "2017-05-12T12:34+01:00[Europe/Berlin]" to parse("2017-05-12T12:34+01:00[Europe/Berlin]"),
            "2017-05-12T12:34:56+01:00[Europe/Paris]" to parse("2017-05-12T12:34:56+01:00[Europe/Paris]"),
            "2017-05-12T12:34:56.123+01:00[Europe/London]" to parse("2017-05-12T12:34:56.123+01:00[Europe/London]")
    )

    override fun getDefaultFormatValue() = "2017-05-12T12:34+01:00[Europe/Berlin]" to parse("2017-05-12T12:34+01:00[Europe/Berlin]")

    override fun getCustomFormat() = "dd.MM.uuuu HH:mm 'Uhr' X VV"
    override fun getCustomFormatValue() = "12.05.2017 12:34 Uhr +01 Europe/Berlin" to parse("2017-05-12T12:34+01:00[Europe/Berlin]")
    override fun getMalformedCustomFormat() = "dd.MM.uuuu HH:mm X V"

}
