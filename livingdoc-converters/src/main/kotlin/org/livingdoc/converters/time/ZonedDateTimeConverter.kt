package org.livingdoc.converters.time

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


open class ZonedDateTimeConverter : AbstractTemporalConverter<ZonedDateTime>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): ZonedDateTime =
        ZonedDateTime.parse(value, formatter)

    override fun canConvertTo(targetType: Class<*>) = ZonedDateTime::class.java == targetType

}
