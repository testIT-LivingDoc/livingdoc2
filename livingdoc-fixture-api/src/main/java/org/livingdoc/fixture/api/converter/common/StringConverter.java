package org.livingdoc.fixture.api.converter.common;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.converter.TypeConverter;


public class StringConverter implements TypeConverter<String> {

    @Override
    public String convert(String value, AnnotatedElement element) {
        return value;
    }

}
