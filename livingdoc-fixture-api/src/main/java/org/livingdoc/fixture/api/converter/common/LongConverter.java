package org.livingdoc.fixture.api.converter.common;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.exceptions.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class LongConverter implements TypeConverter<Long> {

    @Override
    public Long convert(String value, AnnotatedElement element) throws ConversionException {
        try {
            String trimmedValue = value.trim();
            return Long.valueOf(trimmedValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
