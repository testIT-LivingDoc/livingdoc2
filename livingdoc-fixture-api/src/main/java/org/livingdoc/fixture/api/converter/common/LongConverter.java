package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class LongConverter implements TypeConverter<Long> {

    @Override
    public Long convert(String value) throws ConversionException {
        try {
            String trimmedValue = value.trim();
            return Long.valueOf(trimmedValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

}
