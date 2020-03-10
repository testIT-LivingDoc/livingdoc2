package org.livingdoc.api.conversion

import java.lang.reflect.AnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Classes implementing this interface are used by LivingDoc to convert a single [String] value into the defined target
 * type. The converted value is used by LivingDoc as a parameter to invoke a methods or when assigning a new value to
 * a field.
 *
 * The location where a [TypeConverter] is used is declared with the [Converter] annotation. Some [TypeConverter]
 * implementation can be configured by annotating the target of the [Converter] annotation with additional annotations
 * like [Format].
 *
 * @param T the target type
 * @see Converter
 * @see Format
 *
 * @since 2.0
</T> */
interface TypeConverter<T> {
    /**
     * Converts the given [String] value into an instance of this [TypeConverter]'s target type.
     *
     * The [AnnotatedElement] is the element who's value should be converted (`method` or `field`) or `null`. It can be
     * used to access additional configuration annotations like [Format].
     *
     * The only exception this method is allowed to throw is a [ConversionException]. If any other runtime exception is
     * thrown during the conversion it has to be either packaged as a [ConversionException] or LivingDoc might exhibit
     * undefined behavior.
     *
     * @param value the value to convert
     * @param type the type to convert to
     * @param context the context for annotations
     * @return the converted target instance
     * @throws ConversionException in case the conversion failed
     * @since 2.0
     */
    @Throws(ConversionException::class)
    fun convert(value: String, type: KType, context: Context): T

    /**
     * Checks whether this [TypeConverter] can convert strings to the given type.
     *
     * @param targetType the type to convert to
     * @return true if the converter can convert to the given type
     * @since 2.0
     */
    fun canConvertTo(targetType: KClass<*>): Boolean
}
