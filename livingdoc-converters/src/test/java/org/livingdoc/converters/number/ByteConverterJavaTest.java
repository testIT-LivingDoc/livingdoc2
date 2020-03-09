package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;

import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class ByteConverterJavaTest {

    ByteConverter cut = new ByteConverter();

    @Test
    void converterCanConvertedToJavaByte() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Byte.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Byte value = TypeConverterExtensionKt.convertValueOnly(cut, "42");
        assertThat(value).isEqualTo(( byte ) 42);
    }

}
