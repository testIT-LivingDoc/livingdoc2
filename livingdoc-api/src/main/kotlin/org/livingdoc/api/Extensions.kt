package org.livingdoc.api

import org.livingdoc.jvm.api.extension.Extension
import kotlin.reflect.KClass

/**
 * Activate Extensions for the annotated scope and all nested scopes. This includes all nested classes and Fixtures.
 * [Extensions][Extension] are used in the order they are activated, so Extensions from the enclosing scopes are called
 * first.
 */
@Target(AnnotationTarget.CLASS)
annotation class Extensions(vararg val value: KClass<Extension>)
