package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class IntegerConverter implements TypeConverter<Integer> {

    @Override
    public Integer convert(String value) {
        return Integer.valueOf(value);
    }

}
