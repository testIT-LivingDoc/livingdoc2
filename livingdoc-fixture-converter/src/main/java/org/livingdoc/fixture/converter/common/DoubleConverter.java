package org.livingdoc.fixture.converter.common;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class DoubleConverter implements TypeConverter<Double> {

    @Override
    public Double convert(String value, AnnotatedElement element) throws ConversionException {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
