package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;


class ZonedDateTimeConverterJavaTest {

    ZonedDateTimeConverter cut = new ZonedDateTimeConverter();

    @Test
    void converterCanConvertedToJavaZonedDateTime() {
        assertThat(cut.canConvertTo(ZonedDateTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime value = cut.convert(now.toString());
        assertThat(value).isEqualTo(now);
    }

}
