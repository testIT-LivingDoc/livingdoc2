package org.livingdoc.repositories

/**
 * A ParseException is thrown when a [Document] can not be parsed
 */
class ParseException(message: String) : RuntimeException(message)
