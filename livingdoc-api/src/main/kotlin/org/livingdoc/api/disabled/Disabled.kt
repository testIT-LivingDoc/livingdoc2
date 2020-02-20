package org.livingdoc.api.disabled

/**
 * Disabled is used to signal that the annotated test class is currently disabled and should not be
 * executed.
 */
@Target(AnnotationTarget.CLASS)
annotation class Disabled(val value: String = "")
