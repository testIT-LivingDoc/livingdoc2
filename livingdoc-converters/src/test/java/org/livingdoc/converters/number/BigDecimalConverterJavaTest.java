package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class BigDecimalConverterJavaTest {

    BigDecimalConverter cut = new BigDecimalConverter();
    private Locale defaultLocale;

    @BeforeEach
    void setup() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterEach
     void teardown() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    void converterCanConvertedToJavaBigDecimal() {
        assertThat(cut.canConvertTo(BigDecimal.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigDecimal value = cut.convert("42.01", null, null);
        assertThat(value).isEqualTo(BigDecimal.valueOf(42.01d));
    }

}
