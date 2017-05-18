package org.livingdoc.fixture.api.converter;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.ConversionException;


public interface TypeConverter<T> {

    default T convert(String value) {
        return convert(value, null);
    }

    T convert(String value, AnnotatedElement element) throws ConversionException;

}
