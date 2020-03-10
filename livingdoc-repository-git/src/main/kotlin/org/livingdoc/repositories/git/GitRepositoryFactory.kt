package org.livingdoc.repositories.git

import org.livingdoc.config.YamlUtils
import org.livingdoc.repositories.DocumentRepositoryFactory

/**
 * GitRepositoryFactory creates [GitRepositories][GitRepository] to lookup documents in remote git repositories
 */
class GitRepositoryFactory : DocumentRepositoryFactory<GitRepository> {
    override fun createRepository(name: String, configData: Map<String, Any>): GitRepository {
        val config = YamlUtils.toObject(configData, GitRepositoryConfig::class)

        return GitRepository(name, config)
    }
}
