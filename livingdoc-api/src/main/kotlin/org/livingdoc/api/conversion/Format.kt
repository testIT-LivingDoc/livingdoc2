package org.livingdoc.api.conversion

/**
 * Defines a format to be interpreted and used by a [TypeConverter]. The syntax of that format depends on the
 * [TypeConverter]. Generally these formats tend to be regular expressions or date formats. For details you'll have to
 * take a look at the used [TypeConverter]'s documentation.
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class Format(val value: String)
