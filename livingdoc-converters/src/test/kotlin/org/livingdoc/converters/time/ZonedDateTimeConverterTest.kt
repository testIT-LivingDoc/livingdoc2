package org.livingdoc.converters.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import java.time.ZonedDateTime
import java.time.ZonedDateTime.parse

internal class ZonedDateTimeConverterTest : TemporalConverterContract<ZonedDateTime>(), DefaultTypeConverterContract {

    override val cut = ZonedDateTimeConverter()

    override val validInputVariations = mapOf(
        "2017-05-12T12:34+01:00[Europe/Berlin]" to parse("2017-05-12T12:34+01:00[Europe/Berlin]"),
        "2017-05-12T12:34:56+01:00[Europe/Paris]" to parse("2017-05-12T12:34:56+01:00[Europe/Paris]"),
        "2017-05-12T12:34:56.123+01:00[Europe/London]" to parse("2017-05-12T12:34:56.123+01:00[Europe/London]")
    )

    override val defaultFormatValue =
        "2017-05-12T12:34+01:00[Europe/Berlin]" to parse("2017-05-12T12:34+01:00[Europe/Berlin]")

    override val customFormat = "dd.MM.uuuu HH:mm 'Uhr' X VV"
    override val customFormatValue =
        "12.05.2017 12:34 Uhr +01 Europe/Berlin" to parse("2017-05-12T12:34+01:00[Europe/Berlin]")
    override val malformedCustomFormat = "dd.MM.uuuu HH:mm X V"

    @Test fun `converter can converted to ZonedDateTime`() {
        assertThat(cut.canConvertTo(ZonedDateTime::class.java)).isTrue()
    }
}
