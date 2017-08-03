package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class IntegerConverterJavaTest {

    IntegerConverter cut = new IntegerConverter();

    @Test
    void converterCanConvertedToJavaInteger() {
        assertThat(cut.canConvertTo(Integer.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Integer value = cut.convert("42");
        assertThat(value).isEqualTo(42);
    }

}
