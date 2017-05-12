package org.livingdoc.fixture.api.converter.time;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class LocalTimeConverter extends AbstractTemporalConverter<LocalTime> {

    @Override
    DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ISO_TIME;
    }

    @Override
    LocalTime parse(String value, DateTimeFormatter formatter) {
        return LocalTime.parse(value, formatter);
    }

}
