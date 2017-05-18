package org.livingdoc.fixture.converter.time;

import java.lang.reflect.AnnotatedElement;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.livingdoc.fixture.api.converter.Format;
import org.livingdoc.fixture.api.converter.TypeConverter;
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException;
import org.livingdoc.fixture.converter.exceptions.ValueFormatException;


abstract class AbstractTemporalConverter<T> implements TypeConverter<T> {

    @Override
    public T convert(String value, AnnotatedElement element) throws ValueFormatException, MalformedFormatException {
        DateTimeFormatter formatter = null;
        try {
            formatter = Optional.ofNullable(element)
                .map(e -> e.getAnnotation(Format.class))
                .map(Format::value)
                .map(DateTimeFormatter::ofPattern)
                .orElseGet(this::getDefaultFormatter);
            return parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new ValueFormatException(value, String.valueOf(formatter), e);
        } catch (IllegalArgumentException e) {
            throw new MalformedFormatException(e);
        }
    }

    abstract DateTimeFormatter getDefaultFormatter();

    abstract T parse(String value, DateTimeFormatter formatter);

}
