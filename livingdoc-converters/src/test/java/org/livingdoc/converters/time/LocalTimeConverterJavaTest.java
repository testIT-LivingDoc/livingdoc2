package org.livingdoc.converters.time;

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
        LocalTime value = cut.convert(now.toString(), null, null);
        assertThat(value).isEqualTo(now);
    }

}
