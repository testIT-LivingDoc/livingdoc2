package org.livingdoc.repositories

interface DocumentRepositoryFactory<out T : DocumentRepository> {

    fun createRepository(name: String, configData: Map<String, Any>): T
}