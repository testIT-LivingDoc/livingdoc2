package org.livingdoc.fixture.api.converter.common;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class DoubleConverter implements TypeConverter<Double> {

    @Override
    public Double convert(String value) {
        return Double.valueOf(value);
    }

}
