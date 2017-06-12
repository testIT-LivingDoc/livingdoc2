package org.livingdoc.fixture.converter.number

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

abstract class AbstractNumberConverter<T : Number> : TypeConverter<T> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): T {
        val locale = getLocale(element)
        val format = getFormat(locale)
        configureFormat(format)
        val number = parse(format, value)
        return convertToTarget(number)
    }

    private fun getFormat(locale: Locale): DecimalFormat {
        return DecimalFormat.getNumberInstance(locale) as DecimalFormat
    }

    open protected fun configureFormat(format: DecimalFormat) {
        // hook
    }

    private fun getLocale(element: AnnotatedElement?): Locale {
        val customLocale = element
                ?.getAnnotation(Language::class.java)
                ?.value
                ?.let { Locale.forLanguageTag(it) }
        return customLocale ?: Locale.getDefault(Locale.Category.FORMAT)
    }

    private fun parse(format: DecimalFormat, value: String): Number {
        try {
            val processedValue = preProcess(value)
            val trimmedValue = processedValue.trim()
            return format.parse(trimmedValue)
        } catch (e: ParseException) {
            throw ConversionException("not a number value: '$value'", e)
        }
    }

    open protected fun preProcess(value: String): String = value

    abstract protected fun convertToTarget(number: Number): T

}
