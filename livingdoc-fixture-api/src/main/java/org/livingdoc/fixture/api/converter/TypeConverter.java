package org.livingdoc.fixture.api.converter;

public interface TypeConverter<T> {

    T convert(String value) throws ConversionException;

}
