package org.livingdoc.fixture.converter.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;


class LocalTimeConverterJavaTest {

    LocalTimeConverter cut = new LocalTimeConverter();

    @Test
    void converterCanConvertedToJavaLocalTime() {
        assertThat(cut.canConvertTo(LocalTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalTime now = LocalTime.now();
        LocalTime value = cut.convert(now.toString());
        assertThat(value).isEqualTo(now);
    }

}
