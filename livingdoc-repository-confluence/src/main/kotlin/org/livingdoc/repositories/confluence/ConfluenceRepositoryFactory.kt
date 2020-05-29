package org.livingdoc.repositories.confluence

import org.livingdoc.config.YamlUtils
import org.livingdoc.repositories.DocumentRepositoryFactory

/**
 * This Factory is used to create a ConfluenceRepository. This Factory must be specified in the livingdoc.yml to use the
 * Confluence Repository. For more details about the {@see ConfluenceRepository} see it's documentation.
 */
class ConfluenceRepositoryFactory : DocumentRepositoryFactory<ConfluenceRepository> {
    override fun createRepository(name: String, configData: Map<String, Any>): ConfluenceRepository {
        val config = YamlUtils.toObject(configData, ConfluenceRepositoryConfig::class)
        return ConfluenceRepository(name, config)
    }
}
