package org.livingdoc.converters.time

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.Format
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.MalformedFormatException
import org.livingdoc.converters.exceptions.ValueFormatException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.Temporal
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * The AbstractTemporalConverter is the abstraction of the time converters.
 */
abstract class AbstractTemporalConverter<T : Temporal> : TypeConverter<T> {

    /**
     * This function takes the given value as a string, retrieves the formater for the annotated element
     * and calls the parse function which calls the function that is implemented by the actual Converters.
     */
    @Throws(ValueFormatException::class, MalformedFormatException::class)
    override fun convert(value: String, type: KType, context: Context): T {
        val formatter = getDateTimeFormatter(context)
        return parse(value, formatter)
    }

    private fun getDateTimeFormatter(context: Context): DateTimeFormatter {
        try {
            return context.element.findAnnotation<Format>()
                ?.value
                ?.let { DateTimeFormatter.ofPattern(it) } ?: defaultFormatter()
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
