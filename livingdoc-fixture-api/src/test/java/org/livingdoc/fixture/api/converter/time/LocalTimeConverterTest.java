package org.livingdoc.fixture.api.converter.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.livingdoc.fixture.api.converter.exceptions.MalformedFormatException;
import org.livingdoc.fixture.api.converter.Format;
import org.livingdoc.fixture.api.converter.exceptions.ValueFormatException;


class LocalTimeConverterTest {

    LocalTimeConverter cut = new LocalTimeConverter();

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "12:34", "12:34:56", "12:34:56+01:00" })
    void timesAreConvertedCorrectly(String value) {
        LocalTime parsed = LocalTime.parse(value, DateTimeFormatter.ISO_TIME);
        assertThat(cut.convert(value)).isEqualTo(parsed);
    }

    @Nested
    class FormatOverride {

        @Test
        void defaultFormatAssumedIfNoElementGiven() {
            LocalTime time = cut.convert("12:34", null);
            assertThat(time).isEqualTo("12:34:00");
        }

        @Test
        void defaultFormatAssumedIfNoAnnotationPresent() {
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(null);

            LocalTime time = cut.convert("12:34", element);
            assertThat(time).isEqualTo("12:34:00");
        }

        @Test
        void patternCanBeOverriddenViaAnnotation() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(format.value()).willReturn("HH:mm 'Uhr'");
            given(element.getAnnotation(Format.class)).willReturn(format);

            LocalTime time = cut.convert("12:34 Uhr", element);
            assertThat(time).isEqualTo("12:34:00");
        }

        @Test
        void userSpecifiesMalformedPattern() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(format);
            given(format.value()).willReturn("HH:mm V");

            assertThrows(MalformedFormatException.class, () -> {
                cut.convert("12:34", element);
            });
        }

    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ValueFormatException.class, () -> cut.convert("not Time"));
    }

}
