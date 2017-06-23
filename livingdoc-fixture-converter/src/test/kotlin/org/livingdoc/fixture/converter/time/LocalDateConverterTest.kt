package org.livingdoc.fixture.converter.time

import java.time.LocalDate
import java.time.LocalDate.parse


internal class LocalDateConverterTest : TemporalConverterContract<LocalDate>() {

    override val cut = LocalDateConverter()

    override val validInputVariations = mapOf(
            "2017-05-12" to parse("2017-05-12")
    )

    override val defaultFormatValue = "2017-05-12" to parse("2017-05-12")

    override val customFormat = "dd.MM.uuuu"
    override val customFormatValue = "12.05.2017" to parse("2017-05-12")
    override val malformedCustomFormat = "dd.MM.uuuu V"

}
