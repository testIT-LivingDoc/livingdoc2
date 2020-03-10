package org.livingdoc.api.conversion

/**
 * Overrides the used language ([java.util.Locale]) for the annotated field or parameter.
 * A full list of [TypeConverter] considering this annotation can be found in the documentation.
 * The [value] has to be specified according to the [BCP 47](https://tools.ietf.org/html/bcp47)
 * standard.
 *
 * **Examples for this are:** `de`, `de-DE`, `en-US` etc.
 *
 * @see java.util.Locale.forLanguageTag
 * @since 2.0
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class Language(val value: String)
