package org.livingdoc.converters.collection;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConvertersTestFixtures;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class SetConverterJavaTest {

    SetConverter cut = new SetConverter();

    @Test
    void converterCanConvertedToJavaSet() {
        assertThat(cut.canConvertTo(Set.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        Set<Boolean> expected = new HashSet<>(Arrays.asList(true, false, false, true));
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("setField");

        Set<?> converted = cut.convert("true, false, false, true", field, null);
        assertThat(expected).isEqualTo(converted);
    }
}
