package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException

/**
 * This is used to split a string by a limiter
 */
internal object Tokenizer {

    /**
     * This function splits a string into a list of Strings by separating whenever the provided delimiter is found
     * @param value the string that should be splitted
     * @param delimiter the delimiting string sequence
     */
    fun tokenizeToStringList(value: String, delimiter: String = ",") = value.split(delimiter).map { it.trim() }

    /**
     * This function splits a string into a Map of String pairs by using two different delimiters
     * @param value the string that should be splitted
     * @param pairDelimiter the delimiting string sequence for distinguishing pairs
     * @param valueDelimiter the delimiting string sequence for distinguishing key and value of a pair
     */
    fun tokenizeToMap(value: String, pairDelimiter: String = ";", valueDelimiter: String = ","): Map<String, String> {
        val pairs = tokenizeToStringList(value, pairDelimiter)
        return pairs.map { pairString ->
            val split = tokenizeToStringList(pairString, valueDelimiter)
            if (split.size != 2) throw ConversionException("'$pairString' is not a valid Pair")
            split[0] to split[1]
        }.toMap()
    }
}
