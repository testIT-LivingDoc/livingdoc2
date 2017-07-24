package utils

import org.livingdoc.api.conversion.TypeConverter

fun  <T> TypeConverter<T>.convert(value: String): T? {
        return convert(value, null, null)
}
