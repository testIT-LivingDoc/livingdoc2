package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class IntegerConverterJavaTest {

    IntegerConverter cut = new IntegerConverter();

    @Test
    void converterCanConvertedToJavaInteger() {
        assertThat(cut.canConvertTo(Integer.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Integer value = TypeConverterExtensionKt.convertValueOnly(cut, "42");
        assertThat(value).isEqualTo(42);
    }

}
