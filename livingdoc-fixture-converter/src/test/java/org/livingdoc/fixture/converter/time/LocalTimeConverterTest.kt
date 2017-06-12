package org.livingdoc.fixture.converter.time

import java.time.LocalTime
import java.time.LocalTime.parse


internal class LocalTimeConverterTest : TemporalConverterContract<LocalTime>() {

    override val cut = LocalTimeConverter()

    override val validInputVariations = mapOf(
            "12:34" to parse("12:34"),
            "12:34:56" to parse("12:34:56")
    )

    override val defaultFormatValue = "12:34" to parse("12:34")

    override val customFormat = "HH:mm 'Uhr'"
    override val customFormatValue = "12:34 Uhr" to parse("12:34:00")
    override val malformedCustomFormat = "HH:mm V"

}
