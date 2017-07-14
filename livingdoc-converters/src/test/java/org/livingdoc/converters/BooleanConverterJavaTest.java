package org.livingdoc.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class BooleanConverterJavaTest {

    BooleanConverter cut = new BooleanConverter();

    @Test
    void converterCanConvertedToJavaBoolean() {
        assertThat(cut.canConvertTo(Boolean.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Boolean value = cut.convert("true");
        assertThat(value).isTrue();
    }

}
