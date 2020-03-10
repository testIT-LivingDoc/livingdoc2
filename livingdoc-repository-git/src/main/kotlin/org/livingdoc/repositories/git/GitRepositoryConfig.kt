package org.livingdoc.repositories.git

import org.livingdoc.repositories.cache.CacheConfiguration
import org.livingdoc.repositories.cache.CacheHelper

/**
 * This class contains the configuration for loading files from a remote git repository
 */
data class GitRepositoryConfig(
    var remoteUri: String = "",
    var username: String = "",
    var password: String = "",
    var cache: CacheConfiguration = CacheConfiguration(
        path = "livingdoc/build/git",
        cachePolicy = CacheHelper.CACHE_ALWAYS
    )
)
