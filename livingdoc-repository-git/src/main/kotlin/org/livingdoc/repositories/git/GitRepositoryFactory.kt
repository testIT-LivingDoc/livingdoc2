package org.livingdoc.repositories.git

import org.livingdoc.repositories.DocumentRepositoryFactory

class GitRepositoryFactory : DocumentRepositoryFactory<GitRepository> {
    override fun createRepository(name: String, configData: Map<String, Any>): GitRepository {
        return GitRepository()
    }
}
