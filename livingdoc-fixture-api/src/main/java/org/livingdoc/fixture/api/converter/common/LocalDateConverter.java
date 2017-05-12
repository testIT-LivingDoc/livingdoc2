package org.livingdoc.fixture.api.converter.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.TypeConverter;

public class LocalDateConverter implements TypeConverter<LocalDate> {

    private DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public LocalDate convert(String value) throws ConversionException {
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ConversionException(e);
        }
    }
}
