package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class FloatConverter : TypeConverter<Float> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?): Float {
        return value.toFloatOrNull() ?: throw ConversionException("not a parsable float value: " + value)
    }

}
