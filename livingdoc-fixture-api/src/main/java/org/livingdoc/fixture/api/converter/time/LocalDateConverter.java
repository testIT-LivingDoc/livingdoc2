package org.livingdoc.fixture.api.converter.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class LocalDateConverter extends AbstractTemporalConverter<LocalDate> {

    @Override
    DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ISO_DATE;
    }

    @Override
    LocalDate parse(String value, DateTimeFormatter formatter) {
        return LocalDate.parse(value, formatter);
    }

}
