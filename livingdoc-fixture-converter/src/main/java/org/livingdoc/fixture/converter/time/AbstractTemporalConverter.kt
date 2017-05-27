package org.livingdoc.fixture.converter.time

import org.livingdoc.fixture.api.converter.Format
import org.livingdoc.fixture.api.converter.TypeConverter
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException
import org.livingdoc.fixture.converter.exceptions.ValueFormatException
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
