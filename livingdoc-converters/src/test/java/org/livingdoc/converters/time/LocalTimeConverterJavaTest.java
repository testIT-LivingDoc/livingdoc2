package org.livingdoc.converters.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;


class LocalTimeConverterJavaTest {

    LocalTimeConverter cut = new LocalTimeConverter();

    @Test
    void converterCanConvertedToJavaLocalTime() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(LocalTime.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        LocalTime now = LocalTime.now();
        LocalTime value = TypeConverterExtensionKt.convertValueOnly(cut, now.toString());
        assertThat(value).isEqualTo(now);
    }

}
