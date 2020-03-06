package org.livingdoc.converters.time;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class LocalDateConverterJavaTest {

    LocalDateConverter cut = new LocalDateConverter();

    @Test
    void converterCanConvertedToJavaLocalDate() {
        assertThat(cut.canConvertTo(LocalDate.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalDate now = LocalDate.now();
        LocalDate value = TypeConverterExtensionKt.convertValueOnly(cut, now.toString());
        assertThat(value).isEqualTo(now);
    }

}
