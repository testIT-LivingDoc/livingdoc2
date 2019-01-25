package org.livingdoc.repositories.config

data class RepositoryDefinition(
    var name: String = "default",
    var factory: String? = null,
    var config: Map<String, Any> = emptyMap()
)
