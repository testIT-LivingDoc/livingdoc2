package org.livingdoc.fixture.converter.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalDateTime.parse


internal class LocalDateTimeConverterTest : TemporalConverterContract<LocalDateTime>() {

    override val cut = LocalDateTimeConverter()

    override val validInputVariations = mapOf(
            "2017-05-12T12:34" to parse("2017-05-12T12:34"),
            "2017-05-12T12:34:56" to parse("2017-05-12T12:34:56"),
            "2017-05-12T12:34:56.123" to parse("2017-05-12T12:34:56.123")
    )

    override val defaultFormatValue = "2017-05-12T12:34" to parse("2017-05-12T12:34")

    override val customFormat = "dd.MM.uuuu HH:mm 'Uhr'"
    override val customFormatValue = "12.05.2017 12:34 Uhr" to parse("2017-05-12T12:34")
    override val malformedCustomFormat = "dd.MM.uuuu HH:mm V"

    @Test
    fun `converter can converted to LocalDateTime`() {
        assertThat(cut.canConvertTo(LocalDateTime::class.java)).isTrue()
    }

}
