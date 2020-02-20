package org.livingdoc.api.tagging

/**
 * This annotation is used to specify a tag for a test, e.g. to categorize tests by environment, topic or other things.
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class Tag(
    /**
     * The tag as a String
     */
    val value: String
)
