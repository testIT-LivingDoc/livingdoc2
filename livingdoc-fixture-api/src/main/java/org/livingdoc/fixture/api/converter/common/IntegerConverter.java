package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class IntegerConverter implements TypeConverter<Integer> {

    @Override
    public Integer convert(String value) throws ConversionException {
        try {
            String trimmedValue = value.trim();
            return Integer.valueOf(trimmedValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
