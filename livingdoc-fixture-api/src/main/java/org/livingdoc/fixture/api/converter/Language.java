package org.livingdoc.fixture.api.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Overrides the used language ({@link java.util.Locale}) for the annotated field or parameter.
 * A full list of {@link TypeConverter} considering this annotation can be found in the documentation.
 * The {@link #value()} has to be specified according to the <a href="https://tools.ietf.org/html/bcp47">BCP 47</a>
 * standard.
 * <p>
 * <b>Examples for this are:</b> {@code de}, {@code de-DE}, {@code en-US} etc.
 *
 * @see java.util.Locale#forLanguageTag(String)
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface Language {
    String value();
}
