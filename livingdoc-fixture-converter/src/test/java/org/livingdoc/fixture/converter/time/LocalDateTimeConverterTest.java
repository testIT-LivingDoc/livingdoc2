package org.livingdoc.fixture.converter.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.livingdoc.fixture.api.converter.Format;
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException;
import org.livingdoc.fixture.converter.exceptions.ValueFormatException;


class LocalDateTimeConverterTest {

    LocalDateTimeConverter cut = new LocalDateTimeConverter();

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "2017-05-12T12:34", "2017-05-12T12:34:56", "2017-05-12T12:34:56.123" })
    void dateTimesAreConvertedCorrectly(String value) {
        LocalDateTime parsed = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        assertThat(cut.convert(value)).isEqualTo(parsed);
    }

    @Nested
    class FormatOverride {

        @Test
        void defaultFormatAssumedIfNoElementGiven() {
            LocalDateTime time = cut.convert("2017-05-12T12:34", null);
            assertThat(time).isEqualTo("2017-05-12T12:34");
        }

        @Test
        void defaultFormatAssumedIfNoAnnotationPresent() {
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(null);

            LocalDateTime time = cut.convert("2017-05-12T12:34", element);
            assertThat(time).isEqualTo("2017-05-12T12:34");
        }

        @Test
        void patternCanBeOverriddenViaAnnotation() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(format.value()).willReturn("dd.MM.uuuu HH:mm 'Uhr'");
            given(element.getAnnotation(Format.class)).willReturn(format);

            LocalDateTime time = cut.convert("12.05.2017 12:34 Uhr", element);
            assertThat(time).isEqualTo("2017-05-12T12:34");
        }

        @Test
        void userSpecifiesMalformedPattern() {
            Format format = mock(Format.class);
            AnnotatedElement element = mock(AnnotatedElement.class);
            given(element.getAnnotation(Format.class)).willReturn(format);
            given(format.value()).willReturn("dd.MM.uuuu HH:mm V");

            assertThrows(MalformedFormatException.class, () -> {
                cut.convert("12.05.2017 12:34 Uhr", element);
            });
        }

    }

    @Test
    void illegalValueThrowsConversionException() {
        assertThrows(ValueFormatException.class, () -> cut.convert("not Date/Time"));
    }

}
