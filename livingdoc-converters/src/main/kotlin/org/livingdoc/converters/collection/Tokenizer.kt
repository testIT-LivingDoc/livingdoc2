package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException

internal object Tokenizer {

    fun tokenizeToStringList(value: String, delimiter: String = ",") = value.split(delimiter).map { it.trim() }

    fun tokenizeToMap(value: String, pairDelimiter: String = ";", valueDelimiter: String = ","): Map<String, String> {
        val pairs = tokenizeToStringList(value, pairDelimiter)
        return pairs.map {
            val split = tokenizeToStringList(it, valueDelimiter)
            if (split.size != 2) throw ConversionException("'$it' is not a valid Pair")
            split[0] to split[1]
        }.toMap()
    }
}
