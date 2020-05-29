package org.livingdoc.repositories.confluence

import org.livingdoc.repositories.cache.CacheConfiguration

/**
 * The configuration object for the Confluence Repository.
 *
 * @param baseURL The baseURL of the Confluence Server with the format `protocol://host:port`
 * @param path The Context path of the Confluence Server, for `/`
 * @param username The username of a confluence user with access to the Executable Documents.
 * @param password The password of the confluence user given by username.
 */
class ConfluenceRepositoryConfig(
    var baseURL: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var cacheConfig: CacheConfiguration = CacheConfiguration()
)
