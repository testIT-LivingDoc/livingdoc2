package org.livingdoc.api.tagging;


import java.lang.annotation.*;

/**
 * This annotation is used to specify a tag for a test, e.g. to categorize tests by environment, topic or other things.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Tags.class)
public @interface Tag {
    /**
     * The tag as a String
     */
    String value();

}
