package org.livingdoc.fixture.api.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Defines a format to be interpreted and used by a {@link TypeConverter}. The syntax of that format depends on the {@link
 * TypeConverter}. Generally these formats tend to be regular expressions or date formats. For details you'll have to take a
 * look at the used {@link TypeConverter}'s documentation.
 *
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface Format {
    String value();
}
