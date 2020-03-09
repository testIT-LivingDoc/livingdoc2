package org.livingdoc.converters;

import static org.assertj.core.api.Assertions.assertThat;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;


class StringConverterJavaTest {

    StringConverter cut = new StringConverter();

    @Test
    void converterCanConvertedToJavaString() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(String.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        String value = TypeConverterExtensionKt.convertValueOnly(cut, "abc");
        assertThat(value).isEqualTo("abc");
    }

}
