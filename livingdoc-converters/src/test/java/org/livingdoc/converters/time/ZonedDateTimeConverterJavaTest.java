package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;


class ZonedDateTimeConverterJavaTest {

    ZonedDateTimeConverter cut = new ZonedDateTimeConverter();

    @Test
    void converterCanConvertedToJavaZonedDateTime() {
        assertThat(cut.canConvertTo(ZonedDateTime.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime value =  TypeConverterExtensionKt.convertValueOnly(cut, now.toString());
        assertThat(value).isEqualTo(now);
    }

}
