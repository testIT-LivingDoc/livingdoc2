package org.livingdoc.repositories.config

import org.livingdoc.config.ConfigProvider

/**
 * The RepositoryConfiguration contains all RepositoryDefinition used by this run of LivingDoc.
 */
data class RepositoryConfiguration(
    var repositories: List<RepositoryDefinition> = emptyList()
) {
    companion object {
        fun from(configProvider: ConfigProvider): RepositoryConfiguration {
            return configProvider.getConfigAs("repositories", RepositoryConfiguration::class)
        }
    }
}
