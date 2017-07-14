package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


class LocalDateConverterJavaTest {

    LocalDateConverter cut = new LocalDateConverter();

    @Test
    void converterCanConvertedToJavaLocalDate() {
        assertThat(cut.canConvertTo(LocalDate.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalDate now = LocalDate.now();
        LocalDate value = cut.convert(now.toString());
        assertThat(value).isEqualTo(now);
    }

}
