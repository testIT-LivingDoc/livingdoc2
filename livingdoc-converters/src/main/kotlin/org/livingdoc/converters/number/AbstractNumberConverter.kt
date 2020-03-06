package org.livingdoc.converters.number

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Language
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.NumberRangeException
import org.livingdoc.converters.findAnnotation
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import kotlin.reflect.KType

/**
 * The AbstractNumberConverter is the abstraction of the numeric converters.
 */
abstract class AbstractNumberConverter<T : Number> : TypeConverter<T> {

    abstract val lowerBound: T?
    abstract val upperBound: T?

    /**
     * This function takes the given value as a string, parses it to BigDecimal and calls the convertToTarget method
     * that is implemented by the actual Converters.
     */
    @Throws(ConversionException::class)
    override fun convert(value: String, type: KType, context: Context): T {
        val locale = getLocale(context)
        val format = getFormat(locale)
        val number = parse(format, value)
        assertThatNumberIsWithinRangeBoundaries(number)
        return convertToTarget(number)
    }

    private fun getFormat(locale: Locale): DecimalFormat {
        val format = DecimalFormat.getNumberInstance(locale) as DecimalFormat
        format.isParseBigDecimal = true
        return format
    }

    private fun getLocale(context: Context): Locale {
        return context.findAnnotation<Language>()
            ?.value
            ?.let { Locale.forLanguageTag(it) } ?: Locale.getDefault(Locale.Category.FORMAT)
    }

    private fun parse(format: DecimalFormat, value: String): BigDecimal {
        try {
            val processedValue = preProcess(value)
            val trimmedValue = processedValue.trim()
            return format.parse(trimmedValue) as BigDecimal
        } catch (e: ParseException) {
            throw ConversionException("not a number value: '$value'", e)
        }
    }

    private fun preProcess(value: String): String {
        // Fix for Java's DecimalFormat parser:
        //  - only when parsing BigDecimal targets
        //  - BigDecimal values containing "E+" will be cut of at the "+"
        //  - this can be prevented by pre-emotively replacing "E+" with "E"
        return value.replaceFirst("E+", "E", true)
    }

    private fun assertThatNumberIsWithinRangeBoundaries(number: BigDecimal) {
        val belowLowerBound = if (lowerBound != null) number < bigDecimalOf(lowerBound) else false
        val aboveUpperBound = if (upperBound != null) number > bigDecimalOf(upperBound) else false
        if (belowLowerBound || aboveUpperBound) {
            throw NumberRangeException(number, lowerBound, upperBound)
        }
    }

    private fun bigDecimalOf(value: T?) = BigDecimal(value!!.toString())

    /**
     * This function stub is used for the conversion from BigDecimal to the desired type of the actual converter
     */
    protected abstract fun convertToTarget(number: BigDecimal): T
}
