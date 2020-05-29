package org.livingdoc.repositories.cache

/**
 * Contains the configuration for caching documents.
 *
 * Repositories that want to use the caching can use this to configure their
 * configuration.
 */
class CacheConfiguration(
    var path: String = "build/livingdoc/cache",
    var cachePolicy: String = CacheHelper.CACHE_ALWAYS
)
