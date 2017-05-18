package org.livingdoc.fixture.converter.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class StringConverterTest {

    StringConverter cut = new StringConverter();

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = { "", " ", "foo", "foo bar" })
    void anyStringValueIsReturnedAsIs(String value) {
        assertThat(cut.convert(value)).isEqualTo(value);
    }

}
