package org.livingdoc.converters.time

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

/**
 * This converter parses a String to the local time format
 */
open class LocalTimeConverter : AbstractTemporalConverter<LocalTime>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    override fun doParse(value: String, formatter: DateTimeFormatter): LocalTime = LocalTime.parse(value, formatter)

    override fun canConvertTo(targetType: KClass<*>) = LocalTime::class == targetType
}
