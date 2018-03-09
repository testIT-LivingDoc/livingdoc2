package org.livingdoc.converters.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.livingdoc.converters.TypeConvertersTestFixtures;


class ListConverterJavaTest {

    ListConverter cut = new ListConverter();

    @Test
    void converterCanConvertedToJavaList() {
        assertThat(cut.canConvertTo(List.class)).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() throws NoSuchFieldException {
        List<Boolean> expected = Arrays.asList(true, false, false, true);
        Field field = TypeConvertersTestFixtures.NoAnnotations.class.getField("listField");

        List<?> converted = cut.convert("true, false, false, true", field, null);
        assertThat(expected).isEqualTo(converted);
    }
}
