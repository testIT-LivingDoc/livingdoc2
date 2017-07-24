package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class ByteConverterJavaTest {

    ByteConverter cut = new ByteConverter();

    @Test
    void converterCanConvertedToJavaByte() {
        assertThat(cut.canConvertTo(Byte.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Byte value = cut.convert("42", null, null);
        assertThat(value).isEqualTo(( byte ) 42);
    }

}
