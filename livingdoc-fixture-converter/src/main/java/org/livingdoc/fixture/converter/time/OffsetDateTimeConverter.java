package org.livingdoc.fixture.converter.time;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class OffsetDateTimeConverter extends AbstractTemporalConverter<OffsetDateTime> {

    @Override
    DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }

    @Override
    OffsetDateTime parse(String value, DateTimeFormatter formatter) {
        return OffsetDateTime.parse(value, formatter);
    }

}
