package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement

open class MapConverter : AbstractCollectionConverter(), TypeConverter<Map<Any, Any>> {

    val defaultPairSeparator = ";"

    private val KEY_INDEX: Int = 0
    private val VALUE_INDEX: Int = 1

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement, documentClass: Class<*>?): Map<Any, Any> {
        setElementAndDocument(documentClass, element)

        val keyConverter = findTypeConverterForElement(KEY_INDEX)
        val valueConverter = findTypeConverterForElement(VALUE_INDEX)
        val pairs = tokenize(value, defaultPairSeparator)
        val keyValueLists: Array<List<String>> = splitIntoKeyAndValueLists(pairs)

        val convertedKeys = convertToTypedParam(keyConverter, keyValueLists[KEY_INDEX])
        val convertedValues = convertToTypedParam(valueConverter, keyValueLists[VALUE_INDEX])

        return convertToTarget(convertedKeys, convertedValues)
    }

    private fun splitIntoKeyAndValueLists(pairs: List<String>): Array<List<String>> {
        val keys: MutableList<String> = ArrayList()
        val values: MutableList<String> = ArrayList()
        pairs.forEach {
            val split = tokenize(it, defaultSeparator)
            if(split.size < 2) throw ConversionException("'$it' is not a valid Pair")
            keys.add(split[KEY_INDEX])
            values.add(split[1])
        }
        return arrayOf(keys, values)
    }

    fun convertToTarget(keys: List<Any>, values: List<Any>): Map<Any, Any> {
        val convertedMap: MutableMap<Any, Any> = HashMap()
        for (i in keys.indices) {
            convertedMap.put(keys[i], values[i])
        }
        return convertedMap.toMap()
    }

    override fun canConvertTo(targetType: Class<*>?): Boolean {
        val isJavaObjectType = Map::class.java == targetType
        val isKotlinType = Map::class == targetType
        return isJavaObjectType || isKotlinType
    }
}
