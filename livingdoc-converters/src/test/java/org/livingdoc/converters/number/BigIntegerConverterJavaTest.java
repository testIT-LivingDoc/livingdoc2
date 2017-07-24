package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class BigIntegerConverterJavaTest {

    BigIntegerConverter cut = new BigIntegerConverter();

    @Test
    void converterCanConvertedToJavaBigInteger() {
        assertThat(cut.canConvertTo(BigInteger.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigInteger value = cut.convert("42", null, null);
        assertThat(value).isEqualTo(BigInteger.valueOf(42L));
    }

}
