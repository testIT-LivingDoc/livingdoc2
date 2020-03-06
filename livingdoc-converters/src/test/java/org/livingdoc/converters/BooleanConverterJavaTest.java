package org.livingdoc.converters;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BooleanConverterJavaTest {

    BooleanConverter cut = new BooleanConverter();

    @Test
    void converterCanConvertedToJavaBoolean() {
        assertThat(cut.canConvertTo(Boolean.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Boolean value = TypeConverterExtensionKt.convertValueOnly(cut, "true");
        assertThat(value).isTrue();
    }

}
