package org.livingdoc.fixture.converter.common;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class BooleanConverter implements TypeConverter<Boolean> {

    @Override
    public Boolean convert(String value, AnnotatedElement element) throws ConversionException {
        String lowerCaseValue = value.trim().toLowerCase();
        if ("true".equals(lowerCaseValue)) {
            return true;
        }
        if ("false".equals(lowerCaseValue)) {
            return false;
        }
        throw new ConversionException("'" + value + "' cannot be converted to a Boolean");
    }

}
