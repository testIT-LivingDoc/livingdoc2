package org.livingdoc.converters;

import static org.assertj.core.api.Assertions.assertThat;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;


class CharacterConverterJavaTest {

    CharacterConverter cut = new CharacterConverter();

    @Test
    void converterCanConvertedToJavaCharacter() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Character.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Character value = TypeConverterExtensionKt.convertValueOnly(cut, "a");
        assertThat(value).isEqualTo('a');
    }

}
