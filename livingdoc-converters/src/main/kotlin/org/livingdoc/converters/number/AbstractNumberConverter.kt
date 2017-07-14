package org.livingdoc.converters.number

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Language
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.NumberRangeException
import java.lang.reflect.AnnotatedElement
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

abstract class AbstractNumberConverter<T : Number> : TypeConverter<T> {

    abstract val lowerBound: T?
    abstract val upperBound: T?

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): T {
        val locale = getLocale(element)
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

    private fun getLocale(element: AnnotatedElement?): Locale {
        val customLocale = element
                ?.getAnnotation(Language::class.java)
                ?.value
                ?.let { Locale.forLanguageTag(it) }
        return customLocale ?: Locale.getDefault(Locale.Category.FORMAT)
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

    abstract protected fun convertToTarget(number: BigDecimal): T

}
