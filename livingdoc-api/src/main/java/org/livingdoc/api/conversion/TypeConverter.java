package org.livingdoc.api.conversion;

import java.lang.reflect.AnnotatedElement;


/**
 * Classes implementing this interface are used by LivingDoc to convert a single {@link String} value into the defined
 * target type. The converted value is used by LivingDoc as a parameter to invoke a methods or when assigning a new value to
 * a field.
 * <p>
 * The location where a {@link TypeConverter} is used is declared with the {@link Converter} annotation.
 * Some {@link TypeConverter} implementation can be configured by annotating the target of the {@link Converter} annotation
 * with additional annotations like {@link Format}.
 *
 * @param <T> the target type
 * @see Converter
 * @see Format
 * @since 2.0
 */
public interface TypeConverter<T> {

    /**
     * Same as {@link #convert(String, AnnotatedElement)} with the annotated element being {@code null}.
     *
     * @param value the value to convert
     * @return the converted target instance
     * @throws ConversionException in case the conversion failed
     * @since 2.0
     */
    default T convert(String value) throws ConversionException {
        return convert(value, null);
    }

    /**
     * Converts the given {@link String} value into an instance of this {@link TypeConverter}'s target type.
     * <p>
     * The {@link AnnotatedElement} is the element who's value should be converted ({@code method} or {@code field})
     * or {@code null}. It can be used to access additional configuration annotations like {@link Format}.
     * <p>
     * The only exception this method is allowed to throw is a {@link ConversionException}. If any other runtime exception
     * is thrown during the conversion it has to be either packaged as a {@link ConversionException} or LivingDoc might
     * exhibit undefined behavior.
     *
     * @param value the value to convert
     * @param element the element being converted - might be {@code null}!
     * @return the converted target instance
     * @throws ConversionException in case the conversion failed
     * @since 2.0
     */
    T convert(String value, AnnotatedElement element) throws ConversionException;

    /**
     * Checks whether this {@link TypeConverter} can convert strings to the given type.
     *
     * @param targetType the type to convert to
     * @return true if the converter can convert to the given type
     * @since 2.0
     */
    boolean canConvertTo(Class<?> targetType);

}
