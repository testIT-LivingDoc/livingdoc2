package org.livingdoc.converters.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import java.time.LocalTime
import java.time.LocalTime.parse

internal class LocalTimeConverterTest : TemporalConverterContract<LocalTime>(), DefaultTypeConverterContract {

    override val cut = LocalTimeConverter()

    override val validInputVariations = mapOf(
        "12:34" to parse("12:34"),
        "12:34:56" to parse("12:34:56")
    )

    override val defaultFormatValue = "12:34" to parse("12:34")

    override val customFormat = "HH:mm 'Uhr'"
    override val customFormatValue = "12:34 Uhr" to parse("12:34:00")
    override val malformedCustomFormat = "HH:mm V"

    @Test fun `converter can converted to LocalTime`() {
        assertThat(cut.canConvertTo(LocalTime::class)).isTrue()
    }
}
