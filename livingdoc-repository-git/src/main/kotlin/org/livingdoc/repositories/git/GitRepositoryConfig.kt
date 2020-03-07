package org.livingdoc.repositories.git

class GitRepositoryConfig(
    var remoteUri: String = "",
    var path: String = "",
    var localPath: String = "",
    var ref: String = "origin/master"
)
