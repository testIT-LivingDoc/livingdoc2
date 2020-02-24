package org.livingdoc.api

import org.livingdoc.jvm.api.extension.Extension
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Extensions(vararg val value: KClass<Extension>)
