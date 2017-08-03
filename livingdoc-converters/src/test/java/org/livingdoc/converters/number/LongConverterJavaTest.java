package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class LongConverterJavaTest {

    LongConverter cut = new LongConverter();

    @Test
    void converterCanConvertedToJavaLong() {
        assertThat(cut.canConvertTo(Long.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Long value = cut.convert("42");
        assertThat(value).isEqualTo(42L);
    }

}
