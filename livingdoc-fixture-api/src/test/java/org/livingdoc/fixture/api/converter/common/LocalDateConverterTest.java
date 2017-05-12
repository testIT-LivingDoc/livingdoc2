package org.livingdoc.fixture.api.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.livingdoc.fixture.api.converter.ConversionException;


class LocalDateConverterTest {

    LocalDateConverter cut = new LocalDateConverter();

    @Test
    void valuesAreConvertedCorrectly() {
        String date = "2017-05-05";
        LocalDate parsed = LocalDate.parse(date);
        assertThat(cut.convert(date)).isEqualTo(parsed);
    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ConversionException.class, () -> cut.convert("no Date"));
    }

}
