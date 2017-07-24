package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;


class LocalDateTimeConverterJavaTest {

    LocalDateTimeConverter cut = new LocalDateTimeConverter();

    @Test
    void converterCanConvertedToJavaLocalDateTime() {
        assertThat(cut.canConvertTo(LocalDateTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime value = cut.convert(now.toString(), null, null);
        assertThat(value).isEqualTo(now);
    }

}
