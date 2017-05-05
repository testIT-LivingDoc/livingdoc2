package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class BooleanConverter implements TypeConverter<Boolean> {

    @Override
    public Boolean convert(String value) {
        return Boolean.valueOf(value);
    }

}
