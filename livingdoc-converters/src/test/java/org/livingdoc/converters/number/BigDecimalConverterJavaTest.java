package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;

import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class BigDecimalConverterJavaTest {

    BigDecimalConverter cut = new BigDecimalConverter();

    @Test
    void converterCanConvertedToJavaBigDecimal() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(BigDecimal.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigDecimal value = TypeConverterExtensionKt.convertValueOnly(cut, "42.01");
        assertThat(value).isEqualTo(BigDecimal.valueOf(42.01d));
    }

}
