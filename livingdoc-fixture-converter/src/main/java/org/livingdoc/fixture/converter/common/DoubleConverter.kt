package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class DoubleConverter : TypeConverter<Double> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): Double {
        return value.toDoubleOrNull() ?: throw ConversionException("not a parsable double value: " + value)
    }

}
