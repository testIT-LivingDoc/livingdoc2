package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class DoubleConverterJavaTest {

    DoubleConverter cut = new DoubleConverter();

    @Test
    void converterCanConvertedToJavaDouble() {
        assertThat(cut.canConvertTo(Double.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Double value = cut.convert("42.01", null, null);
        assertThat(value).isEqualTo(42.01d);
    }

}
