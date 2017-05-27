package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class LongConverter : TypeConverter<Long> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): Long {
        return value.trim().toLongOrNull() ?: throw ConversionException("not a parsable long value: " + value)
    }

}
