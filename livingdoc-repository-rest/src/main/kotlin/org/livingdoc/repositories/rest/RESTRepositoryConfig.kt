package org.livingdoc.repositories.rest

import org.livingdoc.repositories.cache.CacheConfiguration

/**
 * This class implements the configuration for a REST repository
 */
class RESTRepositoryConfig(
    var baseURL: String = "http://localhost/",
    var cacheConfig: CacheConfiguration = CacheConfiguration()
)
