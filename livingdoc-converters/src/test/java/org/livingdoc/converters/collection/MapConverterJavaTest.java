package org.livingdoc.converters.collection;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import org.livingdoc.converters.TypeConvertersTestFixtures;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class MapConverterJavaTest {

    MapConverter cut = new MapConverter();

    @Test
    void converterCanConvertedToJavaMap() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Map.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        Map<String, Boolean> expected = Collections.unmodifiableMap(
                Stream.of(new SimpleEntry<>("true", true), new SimpleEntry<>("false", false))
                        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("mapField");
        KProperty<?> property = ReflectJvmMapping.getKotlinProperty(field);
        Map<?, ?> converted = TypeConverterExtensionKt.convertValueForProperty(cut, "true, true; false, false",
                property);
        assertThat(expected).isEqualTo(converted);
    }
}
