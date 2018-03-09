package org.livingdoc.converters.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConvertersTestFixtures;


class MapConverterJavaTest {

    MapConverter cut = new MapConverter();

    @Test
    void converterCanConvertedToJavaMap() {
        assertThat(cut.canConvertTo(Map.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        Map<String, Boolean> expected = Collections.unmodifiableMap(
            Stream.of(new SimpleEntry<>("true", true), new SimpleEntry<>("false", false))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("mapField");

        Map<?, ?> converted = cut.convert("true, true; false, false", field, null);
        assertThat(expected).isEqualTo(converted);
    }
}
