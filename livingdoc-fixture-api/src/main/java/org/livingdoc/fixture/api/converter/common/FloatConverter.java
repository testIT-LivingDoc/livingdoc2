package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class FloatConverter implements TypeConverter<Float> {

    @Override
    public Float convert(String value) throws ConversionException {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
