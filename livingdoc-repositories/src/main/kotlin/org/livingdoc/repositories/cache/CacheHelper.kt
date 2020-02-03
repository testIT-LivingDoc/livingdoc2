package org.livingdoc.repositories.cache

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Paths
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
        fun cacheInputStream(inputStream: InputStream, path: String) {
            Files.createDirectories(Paths.get(path).parent)
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING)
        }

        /**
         * Gets the cached object from the path and returns the input stream.
         */
        fun getCacheInputStream(path: String): InputStream {
            return FileInputStream(path)
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
            } catch (e: MalformedURLException) {
                return false
            } catch (e: IOException) {
                return false
            }

            return true
        }
    }
}
