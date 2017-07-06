package org.livingdoc.fixture.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class CharacterConverterJavaTest {

    CharacterConverter cut = new CharacterConverter();

    @Test
    void converterCanConvertedToJavaCharacter() {
        assertThat(cut.canConvertTo(Character.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        Character value = cut.convert("a");
        assertThat(value).isEqualTo('a');
    }

}
