package org.livingdoc.converters.collection;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
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
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(Set.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        Set<Boolean> expected = new HashSet<>(Arrays.asList(true, false, false, true));
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("setField");
        KProperty<?> property = ReflectJvmMapping.getKotlinProperty(field);
        Set<?> converted = TypeConverterExtensionKt.convertValueForProperty(cut,"true, false, false, true", property);
        assertThat(expected).isEqualTo(converted);
    }
}
