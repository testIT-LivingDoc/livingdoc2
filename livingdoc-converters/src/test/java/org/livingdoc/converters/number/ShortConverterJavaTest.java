package org.livingdoc.converters.number;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;

import static org.assertj.core.api.Assertions.assertThat;


@EnglishDefaultLocale
class ShortConverterJavaTest {

    ShortConverter cut = new ShortConverter();

    @Test
    void converterCanConvertedToJavaShort() {
        assertThat(cut.canConvertTo(Short.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Short value = TypeConverterExtensionKt.convertValueOnly(cut, "42");
        assertThat(value).isEqualTo((short) 42);
    }

}
