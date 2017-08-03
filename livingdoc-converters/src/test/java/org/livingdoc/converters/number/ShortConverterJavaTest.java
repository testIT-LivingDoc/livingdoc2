package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class ShortConverterJavaTest {

    ShortConverter cut = new ShortConverter();

    @Test
    void converterCanConvertedToJavaShort() {
        assertThat(cut.canConvertTo(Short.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Short value = cut.convert("42");
        assertThat(value).isEqualTo(( short ) 42);
    }

}
