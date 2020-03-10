package org.livingdoc.api.disabled

/**
 * Disabled is used to signal that the annotated test class is currently disabled and should not be executed. This
 * annotation does not prevent the annotated class from being validated.
 */
@Target(AnnotationTarget.CLASS)
annotation class Disabled(val value: String = "")
