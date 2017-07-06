package org.livingdoc.fixture.converter.time

import java.time.LocalTime
import java.time.format.DateTimeFormatter


open class LocalTimeConverter : AbstractTemporalConverter<LocalTime>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): LocalTime = LocalTime.parse(value, formatter)

    override fun canConvertTo(targetType: Class<*>) = LocalTime::class.java == targetType

}
