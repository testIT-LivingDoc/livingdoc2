package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class BigDecimalConverterJavaTest {

    BigDecimalConverter cut = new BigDecimalConverter();

    @Test
    void converterCanConvertedToJavaBigDecimal() {
        assertThat(cut.canConvertTo(BigDecimal.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigDecimal value = cut.convert("42.01");
        assertThat(value).isEqualTo(BigDecimal.valueOf(42.01d));
    }

}
