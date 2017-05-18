package org.livingdoc.fixture.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.livingdoc.fixture.api.converter.ConversionException;


class IntegerConverterTest {

    IntegerConverter cut = new IntegerConverter();

    @Test
    void zeroValueCanBeConverted() {
        Integer result = cut.convert("0");
        assertThat(result).isEqualTo(0);
    }

    @Test
    void smallestIntegerCanBeConverted() {
        String value = String.valueOf(Integer.MIN_VALUE);
        Integer result = cut.convert(value);
        assertThat(result).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void smallIntegerCanBeConverted() {
        String value = "-10000000";
        Integer result = cut.convert(value);
        assertThat(result).isEqualTo(-10000000);
    }

    @Test
    void largestIntegerCanBeConverted() {
        String value = String.valueOf(Integer.MAX_VALUE);
        Integer result = cut.convert(value);
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void largeIntegerCanBeConverted() {
        String value = "10000000";
        Integer result = cut.convert(value);
        assertThat(result).isEqualTo(10000000);
    }

    @Test
    void anyIntegerCanBeConverted() {
        new Random().ints(1000)//
            .forEach(value -> {
                String positiveValue = String.valueOf(value);
                String negativeValue = String.valueOf(-value);
                assertThat(cut.convert(positiveValue)).isEqualTo(value);
                assertThat(cut.convert(negativeValue)).isEqualTo(-value);
            });
    }

    @Test
    void leadingWhitespacesAreRemoved() {
        assertThat(cut.convert(" 1")).isEqualTo(1);
        assertThat(cut.convert("\t1")).isEqualTo(1);
        assertThat(cut.convert("\n1")).isEqualTo(1);
    }

    @Test
    void trailingWhitespacesAreRemoved() {
        assertThat(cut.convert("1 ")).isEqualTo(1);
        assertThat(cut.convert("1\t")).isEqualTo(1);
        assertThat(cut.convert("1\n")).isEqualTo(1);
    }

    @Test
    void illegalFormatThrowsConversionException() {
        assertThrows(ConversionException.class, () -> {
            cut.convert("hello world");
        });
    }

}
