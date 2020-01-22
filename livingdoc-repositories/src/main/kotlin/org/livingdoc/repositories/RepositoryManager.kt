package org.livingdoc.repositories

import org.livingdoc.repositories.config.RepositoryConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * RepositoryManager handles the instantiation, configuration, and lookup for all implementations of
 * [DocumentRepository].
 *
 * @see DocumentRepository
 */
class RepositoryManager {

    companion object {

        private val log: Logger = LoggerFactory.getLogger(RepositoryManager::class.java)

        /**
         * from creates a RepositoryManager from a given [RepositoryConfiguration].
         *
         * @param config the configuration for all [DocumentRepositories][DocumentRepository]
         * @returns a [RepositoryManager] containing all configured in the [RepositoryConfiguration]
         */
        fun from(config: RepositoryConfiguration): RepositoryManager {
            log.debug("creating new repository manager...")
            val repositoryManager = RepositoryManager()
            config.repositories.forEach { (name, factory, configData) ->

                factory ?: throw NoRepositoryFactoryException(name)

                val factoryClass = Class.forName(factory)
                if (DocumentRepositoryFactory::class.java.isAssignableFrom(factoryClass)) {
                    val factoryInstance =
                        factoryClass.getDeclaredConstructor().newInstance() as DocumentRepositoryFactory<*>
                    val repository = factoryInstance.createRepository(name, configData)
                    repositoryManager.registerRepository(name, repository)
                } else {
                    throw NotARepositoryFactoryException(name)
                }
            }
            log.debug("...repository manager successfully created!")
            return repositoryManager
        }
    }

    private val repositoryMap: MutableMap<String, DocumentRepository> = mutableMapOf()

    /**
     * registerRepository registers a new [DocumentRepository] instance with this RepositoryManager
     *
     * @param name the name under which to register the [DocumentRepository]
     * @param repository the [DocumentRepository] to register
     * @throws RepositoryAlreadyRegisteredException if a [DocumentRepository] of this name has already been registered
     */
    fun registerRepository(name: String, repository: DocumentRepository) {
        log.debug("registering document repository '{}' of type {}", name, repository.javaClass.canonicalName)
        if (repositoryMap.containsKey(name)) {
            log.error("duplicate repository definition: '{}'", name)
            throw RepositoryAlreadyRegisteredException(name, repository)
        }
        repositoryMap[name] = repository
    }

    /**
     * @throws RepositoryNotFoundException
     */
    fun getRepository(name: String): DocumentRepository {
        val knownRepositories = repositoryMap.keys
        return repositoryMap[name] ?: throw RepositoryNotFoundException(name, knownRepositories)
    }

    /**
     * RepositoryAlreadyRegisteredException is thrown when a [DocumentRepository] is registered under a name that has
     * already been registered.
     */
    class RepositoryAlreadyRegisteredException(name: String, repository: DocumentRepository) :
        RuntimeException(
            "Document repository with name '$name' already registered! " +
                    "Cant register instance of ${repository.javaClass.canonicalName}"
        )

    /**
     * NoRepositoryFactoryException is thrown when the [RepositoryConfiguration] does not specify a
     * [DocumentRepositoryFactory] for a [DocumentRepository].
     */
    class NoRepositoryFactoryException(name: String) :
        RuntimeException("Repository declaration '$name' does not specify a 'factory' property.")

    /**
     * NotARepositoryFactoryException is thrown when the [RepositoryConfiguration] does specify a class that implements
     * [DocumentRepositoryFactory] for a [DocumentRepository].
     */
    class NotARepositoryFactoryException(name: String) :
        RuntimeException(
            "Repository declaration '$name' does not specify a 'factory' property with a class " +
                    "of type '${DocumentRepositoryFactory::class.java.simpleName}'."
        )

    /**
     * RepositoryNotFoundException is thrown when no [DocumentRepository] can be found in a [RepositoryManager] for a
     * given name.
     */
    class RepositoryNotFoundException(name: String, knownRepositories: Collection<String>) :
        RuntimeException("Repository '$name' not found in manager. Known repositories are: $knownRepositories")
}
