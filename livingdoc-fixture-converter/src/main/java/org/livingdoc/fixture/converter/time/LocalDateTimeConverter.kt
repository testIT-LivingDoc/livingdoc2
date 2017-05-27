package org.livingdoc.fixture.converter.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


open class LocalDateTimeConverter : AbstractTemporalConverter<LocalDateTime>() {
    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): LocalDateTime = LocalDateTime.parse(value, formatter)
}
