package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class FloatConverterJavaTest {

    FloatConverter cut = new FloatConverter();

    @Test
    void converterCanConvertedToJavaFloat() {
        assertThat(cut.canConvertTo(Float.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Float value = cut.convert("42.01", null, null);
        assertThat(value).isEqualTo(42.01f);
    }

}
