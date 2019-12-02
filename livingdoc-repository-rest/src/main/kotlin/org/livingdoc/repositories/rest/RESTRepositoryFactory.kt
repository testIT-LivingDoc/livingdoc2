package org.livingdoc.repositories.rest

import org.livingdoc.config.YamlUtils
import org.livingdoc.repositories.DocumentRepositoryFactory

/**
 * This Factory is used to create a RESTRepository. This Factory must be specified in the livingdoc.yml to use the REST
 * Repository. For more details about the {@see RESTRepository} see it's documentation.
 */
class RESTRepositoryFactory : DocumentRepositoryFactory<RESTRepository> {

    override fun createRepository(name: String, configData: Map<String, Any>): RESTRepository {
        val config = YamlUtils.toObject(configData, RESTRepositoryConfig::class)
        return RESTRepository(name, config)
    }
}
