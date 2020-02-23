package org.livingdoc.jvm.engine.config

/**
 * This data class contains two lists, one for included string tags, one for excluded string tags
 */
data class TaggingDefinition(
    var include: List<String> = emptyList(),
    var exclude: List<String> = emptyList()
)
