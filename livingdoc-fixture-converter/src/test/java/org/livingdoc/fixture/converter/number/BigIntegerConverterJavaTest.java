package org.livingdoc.fixture.converter.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;


class BigIntegerConverterJavaTest {

    BigIntegerConverter cut = new BigIntegerConverter();

    @Test
    void converterCanConvertedToJavaBigInteger() {
        assertThat(cut.canConvertTo(BigInteger.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigInteger value = cut.convert("42");
        assertThat(value).isEqualTo(BigInteger.valueOf(42L));
    }

}
