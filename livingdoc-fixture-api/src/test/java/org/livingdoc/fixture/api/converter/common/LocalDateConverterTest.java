package org.livingdoc.fixture.api.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.livingdoc.fixture.api.converter.ConversionException;


class LocalDateConverterTest {

    LocalDateConverter cut = new LocalDateConverter();

    @Test
    void valuesAreConvertedCorrectly() {
        LocalDate now = LocalDate.parse("2017-05-05");
        String formatted = now.format(DateTimeFormatter.ISO_DATE);
        assertThat(cut.convert(formatted)).isEqualTo(now);
    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ConversionException.class, () -> cut.convert("no Date"));
    }

}
