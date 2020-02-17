package org.livingdoc.api.tagging;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify a tag for a test, e.g. to categorize tests by environment, topic or other things.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    /**
     * The tag as a String
     */
    String value();

}
