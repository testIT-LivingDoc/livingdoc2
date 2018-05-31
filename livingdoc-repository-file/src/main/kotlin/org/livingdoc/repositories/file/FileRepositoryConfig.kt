package org.livingdoc.repositories.file

data class FileRepositoryConfig(
    var documentRoot: String = "documentation",
    var formatType: String? = null
)
