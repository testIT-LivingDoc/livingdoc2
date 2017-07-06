package org.livingdoc.fixture.converter.number;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class ByteConverterJavaTest {

    ByteConverter cut = new ByteConverter();

    @Test
    void converterCanConvertedToJavaByte() {
        assertThat(cut.canConvertTo(Byte.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Byte value = cut.convert("42");
        assertThat(value).isEqualTo(( byte ) 42);
    }

}
