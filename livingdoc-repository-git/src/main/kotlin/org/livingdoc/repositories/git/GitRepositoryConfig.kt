package org.livingdoc.repositories.git

/**
 * This class contains the configuration for loading files from a remote git repository
 */
data class GitRepositoryConfig(
    var remoteUri: String = "",
    var localPath: String = "",
    var username: String = "",
    var password: String = ""
)
