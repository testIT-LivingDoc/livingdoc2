package org.livingdoc.fixture.api.converter.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.livingdoc.fixture.api.converter.exceptions.MalformedFormatException;
import org.livingdoc.fixture.api.converter.Format;
import org.livingdoc.fixture.api.converter.exceptions.ValueFormatException;


class LocalDateConverterTest {

    LocalDateConverter cut = new LocalDateConverter();

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "2017-05-12", "2017-05-12+01:00" })
    void datesAreConvertedCorrectly(String value) {
        LocalDate parsed = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        assertThat(cut.convert(value)).isEqualTo(parsed);
    }

    @Nested
    class FormatOverride {

        @Test
        void defaultFormatAssumedIfNoElementGiven() {
            LocalDate date = cut.convert("2017-05-12", null);
            assertThat(date).isEqualTo("2017-05-12");
        }

        @Test
        void defaultFormatAssumedIfNoAnnotationPresent() {
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(null);

            LocalDate date = cut.convert("2017-05-12", element);
            assertThat(date).isEqualTo("2017-05-12");
        }

        @Test
        void patternCanBeOverriddenViaAnnotation() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(format);
            given(format.value()).willReturn("dd.MM.uuuu");

            LocalDate date = cut.convert("12.05.2017", element);
            assertThat(date).isEqualTo("2017-05-12");
        }

        @Test
        void userSpecifiesMalformedPattern() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(format);
            given(format.value()).willReturn("dd.MM.uuuu V");

            assertThrows(MalformedFormatException.class, () -> {
                cut.convert("2017-05-12", element);
            });
        }

    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ValueFormatException.class, () -> cut.convert("not Date"));
    }

}
