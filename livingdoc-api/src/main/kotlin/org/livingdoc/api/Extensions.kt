package org.livingdoc.api

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Extensions(vararg val value: KClass<*>)
