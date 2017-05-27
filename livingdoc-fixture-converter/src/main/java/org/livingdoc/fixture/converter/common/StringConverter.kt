package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class StringConverter : TypeConverter<String> {

    override fun convert(value: String, element: AnnotatedElement?): String = value

}
