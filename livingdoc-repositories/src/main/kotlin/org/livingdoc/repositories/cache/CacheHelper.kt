package org.livingdoc.repositories.cache

import java.io.FileInputStream
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
class CacheHelper {
    companion object {
        /**
         * Caches the given input stream to a file under the given path.
         */
        fun cacheInputStream(inputStream: InputStream, path: Path) {
            Files.createDirectories(path.parent)
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        }

        /**
         * Gets the cached object from the path and returns the input stream.
         */
        fun getCacheInputStream(path: Path): InputStream {
            return FileInputStream(path.toString())
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
}
