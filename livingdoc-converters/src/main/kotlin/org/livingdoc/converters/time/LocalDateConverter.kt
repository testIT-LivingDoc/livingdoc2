package org.livingdoc.converters.time

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * This converter parses a String to the local date format
 */
open class LocalDateConverter : AbstractTemporalConverter<LocalDate>() {

    override fun defaultFormatter(): DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    override fun doParse(value: String, formatter: DateTimeFormatter): LocalDate = LocalDate.parse(value, formatter)

    override fun canConvertTo(targetType: Class<*>) = LocalDate::class.java == targetType
}
