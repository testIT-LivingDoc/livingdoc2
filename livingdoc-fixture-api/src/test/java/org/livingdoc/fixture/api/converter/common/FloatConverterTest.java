package org.livingdoc.fixture.api.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.livingdoc.fixture.api.converter.exceptions.ConversionException;


class FloatConverterTest {

    FloatConverter cut = new FloatConverter();

    @ParameterizedTest(name = "\"{0}\" is converted to 0.0d")
    @ValueSource(strings = { "0", "0.", "0.0", "0.00", "0.000", "0.0000" })
    void zeroValuesCanBeConverted(String value) {
        Float result = cut.convert(value);
        assertThat(result).isEqualTo(0.0f);
    }

    @Test
    void smallestFloatCanBeConverted() {
        String value = String.valueOf(Float.MIN_VALUE);
        Float result = cut.convert(value);
        assertThat(result).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    void smallFloatCanBeConverted() {
        String value = "-10000000.0000001";
        Float result = cut.convert(value);
        assertThat(result).isEqualTo(-10000000.0000001f);
    }

    @Test
    void largestFloatCanBeConverted() {
        String value = String.valueOf(Float.MAX_VALUE);
        Float result = cut.convert(value);
        assertThat(result).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    void largeFloatCanBeConverted() {
        String value = "10000000.0000001";
        Float result = cut.convert(value);
        assertThat(result).isEqualTo(10000000.0000001f);
    }

    @Test
    void anyFloatCanBeConverted() {
        new Random().doubles(1000)//
            .map(value -> value * (Float.MAX_VALUE - 1.0f))//
            .forEach(value -> {
                float floatValue = ( float ) value;
                String positiveValue = String.valueOf(floatValue);
                String negativeValue = String.valueOf(-floatValue);
                assertThat(cut.convert(positiveValue)).isEqualTo(floatValue);
                assertThat(cut.convert(negativeValue)).isEqualTo(-floatValue);
            });
    }

    @Test
    void leadingWhitespacesAreRemoved() {
        assertThat(cut.convert(" 1.0")).isEqualTo(1.0f);
        assertThat(cut.convert("\t1.0")).isEqualTo(1.0f);
        assertThat(cut.convert("\n1.0")).isEqualTo(1.0f);
    }

    @Test
    void trailingWhitespacesAreRemoved() {
        assertThat(cut.convert("1.0 ")).isEqualTo(1.0f);
        assertThat(cut.convert("1.0\t")).isEqualTo(1.0f);
        assertThat(cut.convert("1.0\n")).isEqualTo(1.0f);
    }

    @Test
    void illegalFormatThrowsConversionException() {
        assertThrows(ConversionException.class, () -> {
            cut.convert("hello world");
        });
    }

}
