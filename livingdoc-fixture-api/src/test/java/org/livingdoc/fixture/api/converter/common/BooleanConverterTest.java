package org.livingdoc.fixture.api.converter.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.livingdoc.fixture.api.converter.ConversionException;


class BooleanConverterTest {

    BooleanConverter cut = new BooleanConverter();

    @ParameterizedTest(name = "\"{0}\" is converted to: {1}")
    @CsvSource({ "true, true", "TRUE, true", "TrUe, true",//
        "false, false", "FALSE, false", "FaLsE, false" })
    void valuesAreConvertedCorrectly(String value, boolean expected) {
        assertThat(cut.convert(value)).isEqualTo(expected);
    }

    @Test
    void leadingWhitespacesAreRemoved() {
        assertThat(cut.convert(" true")).isTrue();
        assertThat(cut.convert("\ttrue")).isTrue();
        assertThat(cut.convert("\ntrue")).isTrue();
    }

    @Test
    void trailingWhitespacesAreRemoved() {
        assertThat(cut.convert("true ")).isTrue();
        assertThat(cut.convert("true\t")).isTrue();
        assertThat(cut.convert("true\n")).isTrue();
    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ConversionException.class, () -> {
            cut.convert("neither");
        });
    }

}
