package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException

internal object Tokenizer {

    fun tokenizeToList(value: String, delimiter: String = ","): List<String> {
        return value.split(delimiters = delimiter).map { it.trim() }
    }

    fun tokenizeToMap(value: String, pairDelimiter: String = ";", valueDelimiter: String = ","): Map<String, String> {
        val pairs = tokenizeToList(value, pairDelimiter)
        return pairs.map {
            val split = tokenizeToList(it, valueDelimiter)
            if (split.size != 2) throw ConversionException("'$it' is not a valid Pair")
            split[0] to split[1]
        }.toMap()
    }
}
