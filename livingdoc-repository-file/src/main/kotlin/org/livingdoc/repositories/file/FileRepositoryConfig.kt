package org.livingdoc.repositories.file

/**
 * FileRepositoryConfig contains the configuration for a [FileRepository].
 *
 * @see FileRepository
 */
data class FileRepositoryConfig(
    /**
     * DocumentRoot is the directory path in which executable documents are located.
     */
    var documentRoot: String = "documentation"
)
