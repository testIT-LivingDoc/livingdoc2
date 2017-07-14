package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;


class OffsetDateTimeConverterJavaTest {

    OffsetDateTimeConverter cut = new OffsetDateTimeConverter();

    @Test
    void converterCanConvertedToJavaOffsetDateTime() {
        assertThat(cut.canConvertTo(OffsetDateTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime value = cut.convert(now.toString());
        assertThat(value).isEqualTo(now);
    }

}
