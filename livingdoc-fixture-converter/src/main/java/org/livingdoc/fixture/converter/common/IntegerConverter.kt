package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class IntegerConverter : TypeConverter<Int> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): Int {
        return value.trim().toIntOrNull() ?: throw ConversionException("not a parsable integer value: " + value)
    }

}
