package org.livingdoc.jvm.engine.config

import org.livingdoc.api.tagging.Tag
import kotlin.reflect.KClass

fun List<KClass<*>>.filterTags(include: List<String>, exclude: List<String>): List<KClass<*>> {
    return this.filter {
        val tags = getTags(it)
        when {
            include.isNotEmpty() && tags.none { tag -> include.contains(tag) } -> false
            tags.any { tag -> exclude.contains(tag) } -> false
            else -> true
        }
    }
}

fun getTags(documentClass: KClass<*>): List<String> {
    return documentClass.annotations.filterIsInstance<Tag>().map { it.value }
}
