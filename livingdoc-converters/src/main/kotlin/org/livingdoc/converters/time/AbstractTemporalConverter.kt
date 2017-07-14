package org.livingdoc.converters.time

import org.livingdoc.api.conversion.Format
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.MalformedFormatException
import org.livingdoc.converters.exceptions.ValueFormatException
import java.lang.reflect.AnnotatedElement
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.Temporal


abstract class AbstractTemporalConverter<T : Temporal> : TypeConverter<T> {

    @Throws(ValueFormatException::class, MalformedFormatException::class)
    override fun convert(value: String, element: AnnotatedElement?): T {
        val formatter = getDateTimeFormatter(element)
        return parse(value, formatter)
    }

    private fun getDateTimeFormatter(element: AnnotatedElement?): DateTimeFormatter {
        try {
            val customFormatter = element
                    ?.getAnnotation(Format::class.java)
                    ?.value
                    ?.let { DateTimeFormatter.ofPattern(it) }
            return customFormatter ?: defaultFormatter()
        } catch (e: IllegalArgumentException) {
            throw MalformedFormatException(e)
        }
    }

    private fun parse(value: String, formatter: DateTimeFormatter): T {
        try {
            return doParse(value, formatter)
        } catch (e: DateTimeParseException) {
            throw ValueFormatException(value, formatter.toString(), e)
        }
    }

    protected abstract fun defaultFormatter(): DateTimeFormatter

    @Throws(DateTimeParseException::class)
    protected abstract fun doParse(value: String, formatter: DateTimeFormatter): T

}
