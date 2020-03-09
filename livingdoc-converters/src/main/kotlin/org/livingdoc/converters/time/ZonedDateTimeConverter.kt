package org.livingdoc.converters.time

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

/**
 * This converter parses a String to the time format with offset from UTC and a zone configuration regarding summer time
 */
open class ZonedDateTimeConverter : AbstractTemporalConverter<ZonedDateTime>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): ZonedDateTime =
        ZonedDateTime.parse(value, formatter)

    override fun canConvertTo(targetType: KClass<*>) = ZonedDateTime::class == targetType
}
