package org.livingdoc.fixture.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.livingdoc.fixture.api.converter.ConversionException;


class DoubleConverterTest {

    DoubleConverter cut = new DoubleConverter();

    @ParameterizedTest(name = "\"{0}\" is converted to 0.0d")
    @ValueSource(strings = { "0", "0.", "0.0", "0.00", "0.000", "0.0000" })
    void zeroValuesCanBeConverted(String value) {
        Double result = cut.convert(value);
        assertThat(result).isEqualTo(0.0d);
    }

    @Test
    void smallestDoubleCanBeConverted() {
        String value = String.valueOf(Double.MIN_VALUE);
        Double result = cut.convert(value);
        assertThat(result).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    void smallDoubleCanBeConverted() {
        String value = "-10000000.0000001";
        Double result = cut.convert(value);
        assertThat(result).isEqualTo(-10000000.0000001d);
    }

    @Test
    void largestDoubleCanBeConverted() {
        String value = String.valueOf(Double.MAX_VALUE);
        Double result = cut.convert(value);
        assertThat(result).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    void largeDoubleCanBeConverted() {
        String value = "10000000.0000001";
        Double result = cut.convert(value);
        assertThat(result).isEqualTo(10000000.0000001d);
    }

    @Test
    void anyDoubleCanBeConverted() {
        new Random().doubles(1000)//
            .map(value -> value * (Double.MAX_VALUE - 1.0d))//
            .forEach(value -> {
                String positiveValue = String.valueOf(value);
                String negativeValue = String.valueOf(-value);
                assertThat(cut.convert(positiveValue)).isEqualTo(value);
                assertThat(cut.convert(negativeValue)).isEqualTo(-value);
            });
    }

    @Test
    void leadingWhitespacesAreRemoved() {
        assertThat(cut.convert(" 1.0")).isEqualTo(1.0d);
        assertThat(cut.convert("\t1.0")).isEqualTo(1.0d);
        assertThat(cut.convert("\n1.0")).isEqualTo(1.0d);
    }

    @Test
    void trailingWhitespacesAreRemoved() {
        assertThat(cut.convert("1.0 ")).isEqualTo(1.0d);
        assertThat(cut.convert("1.0\t")).isEqualTo(1.0d);
        assertThat(cut.convert("1.0\n")).isEqualTo(1.0d);
    }

    @Test
    void illegalFormatThrowsConversionException() {
        assertThrows(ConversionException.class, () -> {
            cut.convert("hello world");
        });
    }

}
