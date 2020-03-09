package org.livingdoc.api.conversion

import kotlin.reflect.KClass

/**
 * Annotating a fixture-class, -method or -field with this annotation will instruct LivingDoc to load all of the
 * specified [TypeConverter] for the corresponding context:
 *
 *  * Annotating a **class** will use the [TypeConverter] for as long as the fixture instance exists. Which is usually
 * for the duration of a single test case. It will be used wherever possible (method parameters and fields)
 * unless another [TypeConverter] - who can handle the same type - is specified on the method or field.
 *  * Annotating a **method** will use the [TypeConverter] just for the conversion of that method's parameters.
 * It will be disregarding for all other methods, except if it is specified there as well.
 *  * Annotating a **field** will use the [TypeConverter] just for the initialization of that field.
 *
 *
 * @since 2.0
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
annotation class Converter(vararg val value: KClass<out TypeConverter<*>>)
