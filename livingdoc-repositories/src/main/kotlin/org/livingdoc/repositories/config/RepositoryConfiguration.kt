package org.livingdoc.repositories.config

import org.livingdoc.config.YamlUtils

/**
 * The RepositoryConfiguration contains all RepositoryDefinition used by this run of LivingDoc.
 */
data class RepositoryConfiguration(
    var repositories: List<RepositoryDefinition> = emptyList()
) {
    companion object {
        fun from(configuration: Map<String, Any>): RepositoryConfiguration {
            return YamlUtils.toObject(configuration, RepositoryConfiguration::class)
        }
    }
}
