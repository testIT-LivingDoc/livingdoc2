package org.livingdoc.repositories

/**
 * A DocumentRepositoryFactory creates [DocumentRepository] instances from a name and configuration data.
 *
 * @see DocumentRepository
 */
interface DocumentRepositoryFactory<out T : DocumentRepository> {
    /**
     * createRepository creates an instance of [DocumentRepository] with a given name and the given configuration data.
     *
     * @param name the name of the [DocumentRepository] instance to create
     * @param configData configurationData for the [DocumentRepository] instance to create
     * @returns an instance of [DocumentRepository]
     */
    fun createRepository(name: String, configData: Map<String, Any>): T
}
