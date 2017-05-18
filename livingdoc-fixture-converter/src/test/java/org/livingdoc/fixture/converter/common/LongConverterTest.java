package org.livingdoc.fixture.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.livingdoc.fixture.api.converter.ConversionException;


class LongConverterTest {

    LongConverter cut = new LongConverter();

    @Test
    void zeroValueCanBeConverted() {
        Long result = cut.convert("0");
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void smallestLongCanBeConverted() {
        String value = String.valueOf(Long.MIN_VALUE);
        Long result = cut.convert(value);
        assertThat(result).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void smallLongCanBeConverted() {
        String value = "-10000000";
        Long result = cut.convert(value);
        assertThat(result).isEqualTo(-10000000L);
    }

    @Test
    void largestLongCanBeConverted() {
        String value = String.valueOf(Long.MAX_VALUE);
        Long result = cut.convert(value);
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void largeLongCanBeConverted() {
        String value = "10000000";
        Long result = cut.convert(value);
        assertThat(result).isEqualTo(10000000L);
    }

    @Test
    void anyLongCanBeConverted() {
        new Random().longs(1000)//
            .forEach(value -> {
                String positiveValue = String.valueOf(value);
                String negativeValue = String.valueOf(-value);
                assertThat(cut.convert(positiveValue)).isEqualTo(value);
                assertThat(cut.convert(negativeValue)).isEqualTo(-value);
            });
    }

    @Test
    void leadingWhitespacesAreRemoved() {
        assertThat(cut.convert(" 1")).isEqualTo(1L);
        assertThat(cut.convert("\t1")).isEqualTo(1L);
        assertThat(cut.convert("\n1")).isEqualTo(1L);
    }

    @Test
    void trailingWhitespacesAreRemoved() {
        assertThat(cut.convert("1 ")).isEqualTo(1L);
        assertThat(cut.convert("1\t")).isEqualTo(1L);
        assertThat(cut.convert("1\n")).isEqualTo(1L);
    }

    @Test
    void illegalFormatThrowsConversionException() {
        assertThrows(ConversionException.class, () -> {
            cut.convert("hello world");
        });
    }

}
