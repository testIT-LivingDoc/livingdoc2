package org.livingdoc.repositories.file

import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.DocumentRepositoryFactory
import org.livingdoc.repositories.config.YamlUtils

class FileRepositoryFactory : DocumentRepositoryFactory<FileRepository> {

    override fun createRepository(name: String, configData: Map<String, Any>): FileRepository {
        val config = YamlUtils.toObject(configData, FileRepositoryConfig::class)
        val format = Class.forName(config.formatType).newInstance() as DocumentFormat
        return FileRepository(name, format, config)
    }

}