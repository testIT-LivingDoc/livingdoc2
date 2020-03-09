package org.livingdoc.converters.number;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;

import static org.assertj.core.api.Assertions.assertThat;


@EnglishDefaultLocale
class LongConverterJavaTest {

    LongConverter cut = new LongConverter();

    @Test
    void converterCanConvertedToJavaLong() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Long.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Long value = TypeConverterExtensionKt.convertValueOnly(cut, "42");
        assertThat(value).isEqualTo(42L);
    }

}
