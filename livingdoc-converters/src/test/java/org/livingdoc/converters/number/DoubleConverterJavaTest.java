package org.livingdoc.converters.number;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;

import static org.assertj.core.api.Assertions.assertThat;


@EnglishDefaultLocale
class DoubleConverterJavaTest {

    DoubleConverter cut = new DoubleConverter();

    @Test
    void converterCanConvertedToJavaDouble() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Double.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Double value = TypeConverterExtensionKt.convertValueOnly(cut, "42.01");
        assertThat(value).isEqualTo(42.01d);
    }

}
