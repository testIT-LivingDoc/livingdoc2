package org.livingdoc.fixture.api.converter.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LocalDateTimeConverter extends AbstractTemporalConverter<LocalDateTime> {

    @Override
    DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    @Override
    LocalDateTime parse(String value, DateTimeFormatter formatter) {
        return LocalDateTime.parse(value, formatter);
    }

}
