package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import java.util.Locale.Category

abstract class AbstractNumberConverter<T : Number> : TypeConverter<T> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): T {
        val locale = getLocale(element)
        val format = DecimalFormat.getNumberInstance(locale)
        val number = parse(format, value)
        return convertToTarget(number)
    }

    private fun getLocale(element: AnnotatedElement?): Locale {
        val customLocale = element
                ?.getAnnotation(Language::class.java)
                ?.value
                ?.let { Locale.forLanguageTag(it) }
        return customLocale ?: Locale.getDefault(Category.FORMAT)
    }

    private fun parse(format: NumberFormat, value: String): Number {
        try {
            val trimmedValue = value.trim()
            return format.parse(trimmedValue)
        } catch (e: ParseException) {
            throw ConversionException("not a number value: '$value'")
        }
    }

    abstract fun convertToTarget(number: Number): T

}
