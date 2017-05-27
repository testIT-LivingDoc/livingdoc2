package org.livingdoc.fixture.api.converter;

import java.lang.reflect.AnnotatedElement;


public interface TypeConverter<T> {

    default T convert(String value) throws ConversionException {
        return convert(value, null);
    }

    T convert(String value, AnnotatedElement element) throws ConversionException;

}
