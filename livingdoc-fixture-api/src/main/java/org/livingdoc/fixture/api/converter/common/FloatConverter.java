package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class FloatConverter implements TypeConverter<Float> {

    @Override
    public Float convert(String value) {
        return Float.valueOf(value);
    }

}
