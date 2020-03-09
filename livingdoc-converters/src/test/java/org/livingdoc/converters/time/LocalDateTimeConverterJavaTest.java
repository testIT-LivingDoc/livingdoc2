package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;


class LocalDateTimeConverterJavaTest {

    LocalDateTimeConverter cut = new LocalDateTimeConverter();

    @Test
    void converterCanConvertedToJavaLocalDateTime() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(LocalDateTime.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime value = TypeConverterExtensionKt.convertValueOnly(cut, now.toString());
        assertThat(value).isEqualTo(now);
    }

}
