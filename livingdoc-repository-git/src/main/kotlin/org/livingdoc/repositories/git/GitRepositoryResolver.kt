package org.livingdoc.repositories.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.livingdoc.repositories.cache.CacheHelper
import org.livingdoc.repositories.cache.InvalidCachePolicyException
import java.io.File
import java.lang.Exception

/**
 * GitRepositoryResolver clones and updates a remote git repository locally
 */
class GitRepositoryResolver(
    private val config: GitRepositoryConfig
) {
    private val credentials =
        if (config.username.isBlank() || config.password.isBlank())
            null
        else
            UsernamePasswordCredentialsProvider(config.username, config.password)

    /**
     * Clones the configured remote git repository into the local path and returns a reference to the local repository
     */
    fun resolve(): Repository {
        val repoDirectory = File(config.cache.path)

        return if (repoDirectory.list().isNullOrEmpty()) {
            // We need to clone the remote repository
            cloneRepository(repoDirectory)
        } else {
            val repository = initializeRepository(repoDirectory)

            when (config.cache.cachePolicy) {
                CacheHelper.CACHE_ALWAYS -> fetchUpdates(repository)
                CacheHelper.CACHE_ONCE -> {
                }
                else -> throw InvalidCachePolicyException(config.cache.cachePolicy)
            }

            repository
        }
    }

    /**
     * Creates a reference to an existing local repository
     */
    private fun initializeRepository(repoDirectory: File): Repository {
        return FileRepositoryBuilder()
            .setBare()
            .setGitDir(repoDirectory)
            .build()
    }

    /**
     * Fetches updates from a remote git repository
     */
    private fun fetchUpdates(repository: Repository) {
        try {
            Git(repository)
                .fetch()
                .setRemote(config.remoteUri)
                .setCredentialsProvider(credentials)
                .call()
        } catch (e: Exception) {
            // swallow the exception and continue
            println(e)
        }
    }

    /**
     * Clones a remote repository to the local path
     */
    private fun cloneRepository(repoDirectory: File): Repository {
        return Git.cloneRepository()
            .setCloneAllBranches(false)
            .setGitDir(repoDirectory)
            .setBare(true)
            .setURI(config.remoteUri)
            .setCredentialsProvider(credentials)
            .call()
            .repository
    }
}
