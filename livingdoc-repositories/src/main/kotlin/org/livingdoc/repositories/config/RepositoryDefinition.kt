package org.livingdoc.repositories.config

import org.livingdoc.repositories.DocumentRepository

/**
 * A RepositoryDefinition describes a single [DocumentRepository] inside a [RepositoryConfiguration].
 *
 * @see DocumentRepository
 * @see RepositoryConfiguration
 */
data class RepositoryDefinition(
    var name: String = "default",
    var factory: String? = null,
    var config: Map<String, Any> = emptyMap()
)
