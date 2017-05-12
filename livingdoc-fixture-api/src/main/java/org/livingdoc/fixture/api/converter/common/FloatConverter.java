package org.livingdoc.fixture.api.converter.common;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.exceptions.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class FloatConverter implements TypeConverter<Float> {

    @Override
    public Float convert(String value, AnnotatedElement element) throws ConversionException {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
