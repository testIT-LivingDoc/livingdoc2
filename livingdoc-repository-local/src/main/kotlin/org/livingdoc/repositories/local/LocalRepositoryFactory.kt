package org.livingdoc.repositories.local

import org.livingdoc.repositories.DocumentRepositoryFactory
import org.livingdoc.repositories.config.YamlUtils

class LocalRepositoryFactory : DocumentRepositoryFactory<LocalRepository> {

    override fun createRepository(name: String, configData: Map<String, Any>): LocalRepository {
        val config = YamlUtils.toObject(configData, LocalRepositoryConfig::class)
        return LocalRepository(name, config)
    }

}