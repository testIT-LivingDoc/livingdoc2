package org.livingdoc.repositories.cache

import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * The CacheHelper helps caching documents and retrieving the cached documents.
 *
 * The repositories that want to use caching can use this helper to implement
 * the caching.
 */
object CacheHelper {
    // Always update cache.
    const val CACHE_ALWAYS = "cacheAlways"
    // Cache if newer version is available. Can be used to reduce mobile data usage.
    const val CACHE_ON_NEW_VERSION = "cacheOnNewVersion"
    // Never use cache.
    const val NO_CACHE = "noCache"
    // Cache once and use this cache.
    const val CACHE_ONCE = "cacheOnce"

    /**
     * Caches the given input stream to a file under the given path.
     * The input stream is closed.
     *
     * @throws java.io.IOException
     */
    fun cacheInputStream(inputStream: InputStream, path: Path) {
        Files.createDirectories(path.parent)
        inputStream.use { Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING) }
    }

    /**
     * Gets the cached object from the path and returns the input stream.
     *
     * @throws java.io.IOException
     */
    fun getCacheInputStream(path: Path): InputStream {
        return Files.newInputStream(path)
    }

    /**
     * Checks whether a file at the given path exists. Assumes the file is
     * a cached document.
     *
     * @return true if there is a file. Otherwise false.
     */
    fun isCached(path: Path): Boolean {
        return Files.exists(path)
    }

    /**
     * Checks whether an active connection to the given url exists.
     *
     * @return false if url is malformed or no connection is possible, true otherwise
     */
    fun hasActiveNetwork(url: String): Boolean {
        try {
            val networkUrl = URL(url)
            val connection: URLConnection = networkUrl.openConnection()
            connection.connect()
        } catch (e: Exception) {
            return false
        }

        return true
    }
}
