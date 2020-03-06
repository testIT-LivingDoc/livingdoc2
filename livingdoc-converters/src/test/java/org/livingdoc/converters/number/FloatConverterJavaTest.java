package org.livingdoc.converters.number;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;

import static org.assertj.core.api.Assertions.assertThat;


@EnglishDefaultLocale
class FloatConverterJavaTest {

    FloatConverter cut = new FloatConverter();

    @Test
    void converterCanConvertedToJavaFloat() {
        assertThat(cut.canConvertTo(Float.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Float value = TypeConverterExtensionKt.convertValueOnly(cut, "42.01");
        assertThat(value).isEqualTo(42.01f);
    }

}
