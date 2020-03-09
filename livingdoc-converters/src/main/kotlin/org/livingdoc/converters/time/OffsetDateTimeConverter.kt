package org.livingdoc.converters.time

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

/**
 * This converter parses a String to the time format with offset from UTC
 */
open class OffsetDateTimeConverter : AbstractTemporalConverter<OffsetDateTime>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): OffsetDateTime =
        OffsetDateTime.parse(value, formatter)

    override fun canConvertTo(targetType: KClass<*>) = OffsetDateTime::class == targetType
}
