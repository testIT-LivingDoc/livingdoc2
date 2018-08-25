package org.livingdoc.repositories.file

import org.livingdoc.repositories.DocumentRepositoryFactory
import org.livingdoc.repositories.config.YamlUtils

class FileRepositoryFactory : DocumentRepositoryFactory<FileRepository> {

    override fun createRepository(name: String, configData: Map<String, Any>): FileRepository {
        val config = YamlUtils.toObject(configData, FileRepositoryConfig::class)
        return FileRepository(name, config)
    }

}