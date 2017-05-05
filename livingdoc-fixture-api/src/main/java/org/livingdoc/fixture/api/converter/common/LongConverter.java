package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class LongConverter implements TypeConverter<Long> {

    @Override
    public Long convert(String value) {
        return Long.valueOf(value);
    }

}
