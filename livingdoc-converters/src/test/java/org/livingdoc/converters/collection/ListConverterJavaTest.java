package org.livingdoc.converters.collection;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConverterExtensionKt;
import org.livingdoc.converters.TypeConvertersTestFixtures;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ListConverterJavaTest {

    ListConverter cut = new ListConverter();

    @Test
    void converterCanConvertedToJavaList() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(List.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        List<Boolean> expected = Arrays.asList(true, false, false, true);
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("listField");
        KProperty<?> property = ReflectJvmMapping.getKotlinProperty(field);
        List<?> converted = TypeConverterExtensionKt.convertValueForProperty(cut, "true, false, false, true", property);
        assertThat(expected).isEqualTo(converted);
    }
}
