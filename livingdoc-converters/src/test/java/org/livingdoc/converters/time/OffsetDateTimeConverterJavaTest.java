package org.livingdoc.converters.time;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class OffsetDateTimeConverterJavaTest {

    OffsetDateTimeConverter cut = new OffsetDateTimeConverter();

    @Test
    void converterCanConvertedToJavaOffsetDateTime() {
        assertThat(cut.canConvertTo(OffsetDateTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime value = TypeConverterExtensionKt.convertValueOnly(cut, now.toString());
        assertThat(value).isEqualTo(now);
    }

}
