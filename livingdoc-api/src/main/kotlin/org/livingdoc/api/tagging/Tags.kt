package org.livingdoc.api.tagging

/**
 * This annotation is used to specify an array of tags for a test, e.g. to categorize tests by environment, topic or
 * other things.
 */
@Target(AnnotationTarget.CLASS)
annotation class Tags(
    /**
     * The tag as a String
     */
    vararg val value: Tag
)
