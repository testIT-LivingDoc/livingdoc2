package org.livingdoc.repositories.rest

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import kotlinx.coroutines.runBlocking
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository
import org.livingdoc.repositories.cache.CacheHelper
import org.livingdoc.repositories.cache.InvalidCachePolicyException
import org.livingdoc.repositories.format.DocumentFormatManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths

/**
 * This implementation of a DocumentRepository uses a remote HTTP server to get the Documents from.
 * The remote host can be configured in the livingdoc.yml config section of the repository. Use baseURL to set the base
 * url of the remote host that hosts the Documents. As default the server on port 80 on the localhost is used as REST
 * Repository.
 */
class RESTRepository(
    private val name: String,
    private val config: RESTRepositoryConfig,
    private val client: HttpClient = HttpClient()
) : DocumentRepository {

    private val log: Logger = LoggerFactory.getLogger(RESTRepository::class.java)

    override fun getDocument(documentIdentifier: String): Document {
        return when (config.cacheConfig.cachePolicy) {
            CacheHelper.CACHE_ALWAYS -> handleCacheAlways(documentIdentifier)
            CacheHelper.CACHE_ONCE -> handleCacheOnce(documentIdentifier)
            CacheHelper.NO_CACHE -> handleNoCache(documentIdentifier)
            else -> throw InvalidCachePolicyException(config.cacheConfig.cachePolicy)
        }
    }

    private fun handleCacheAlways(documentIdentifier: String): Document {
        if (CacheHelper.hasActiveNetwork(config.baseURL)) {
            return getFromRequestAndCache(documentIdentifier)
        }
        return getFromCache(documentIdentifier)
    }

    private fun handleCacheOnce(documentIdentifier: String): Document {
        if (CacheHelper.isCached(Paths.get(config.cacheConfig.path, documentIdentifier))) {
            return getFromCache(documentIdentifier)
        }

        return getFromRequestAndCache(documentIdentifier)
    }

    private fun handleNoCache(documentIdentifier: String): Document {
        return DocumentFormatManager.getFormat("html").parse(getRequest(documentIdentifier))
    }

    private fun getFromRequestAndCache(documentIdentifier: String): Document {
        val request = getRequest(documentIdentifier)
        try {
            CacheHelper.cacheInputStream(request, Paths.get(config.cacheConfig.path, documentIdentifier))
        } catch (e: Exception) {
            throw RESTDocumentNotFoundException(e, documentIdentifier, config.baseURL)
        }
        return getFromCache(documentIdentifier)
    }

    private fun getRequest(documentIdentifier: String): InputStream {
        return runBlocking {
            try {
                log.debug("Get Document from url {}", config.baseURL + documentIdentifier)
                client.get<HttpResponse>(config.baseURL + documentIdentifier).receive<InputStream>()
            } catch (e: IOException) {
                throw RESTDocumentNotFoundException(e, documentIdentifier, config.baseURL)
            } catch (e: ClientRequestException) {
                throw RESTDocumentNotFoundException(e, documentIdentifier, config.baseURL)
            }
        }
    }

    private fun getFromCache(documentIdentifier: String): Document {
        log.debug("Get Document from cache {}", documentIdentifier)
        try {
            val documentStream = CacheHelper.getCacheInputStream(Paths.get(config.cacheConfig.path, documentIdentifier))
            return DocumentFormatManager.getFormat("html").parse(documentStream)
        } catch (e: Exception) {
            throw RESTDocumentNotFoundException(e, documentIdentifier, config.baseURL)
        }
    }
}
