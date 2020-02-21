package org.livingdoc.api.tagging;


import java.lang.annotation.*;

/**
 * This annotation is used to specify an array of tags for a test, e.g. to categorize tests by environment, topic or
 * other things.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags {
    /**
     * The tag as a String
     */
    Tag[] value();

}
