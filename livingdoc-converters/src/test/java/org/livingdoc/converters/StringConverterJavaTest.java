package org.livingdoc.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class StringConverterJavaTest {

    StringConverter cut = new StringConverter();

    @Test
    void converterCanConvertedToJavaString() {
        assertThat(cut.canConvertTo(String.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        String value = cut.convert("abc", null, null);
        assertThat(value).isEqualTo("abc");
    }

}
