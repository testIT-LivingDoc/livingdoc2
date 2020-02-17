package org.livingdoc.engine.config

data class TaggingDefinition(
    var include: List<String> = emptyList(),
    var exclude: List<String> = emptyList()
)
