package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.HttpClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.cache.CacheHelper

class RESTRepositoryUnitTest {
    var testURL: String = "http://localhost/"
    val reponame = "Testing.html"
    val rrf: RESTRepositoryFactory = RESTRepositoryFactory()

    @Test
    fun `create repository - RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        // testing constructor of Rest Repository
        Assertions.assertThat(RESTRepository(reponame, restrepoCfg))
            .isInstanceOf(RESTRepository::class.java).isNotNull
    }

    @Test
    fun `create repository - RESTRepositoryFactory`() {
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        // testing create Repository
        Assertions.assertThat(rrf.createRepository(reponame, configData))
            .isInstanceOf(RESTRepository::class.java).isNotNull
    }

    @Test
    fun `exception is thrown if document could not be found`() {

        val restRepoConfig = RESTRepositoryConfig()
        restRepoConfig.cacheConfig.cachePolicy = CacheHelper.NO_CACHE
        val cut = RESTRepository("test", restRepoConfig)
        assertThrows<RESTDocumentNotFoundException> {
            cut.getDocument("foo-bar.html")
        }
    }

    // to test getDocument there needs to be a host
    var wms = WireMockServer(WireMockConfiguration.options().dynamicPort())

    @Test
    fun `get Document - RESTRepository`() {
        `setup wiremock to test getDocument`()

        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        restrepoCfg.cacheConfig.cachePolicy = CacheHelper.NO_CACHE
        val comparisonRepository = RESTRepository(reponame, restrepoCfg, HttpClient())

        // test retrieval of document
        Assertions.assertThat(comparisonRepository.getDocument(reponame))
            .isInstanceOf(Document::class.java).isNotNull
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

    @Test
    fun `get document - RESTRepositoryFactory`() {
        `setup wiremock to test getDocument`()

        val configData: Map<String, Any> =
            mutableMapOf<String, Any>(
                "baseURL" to testURL,
                "cacheConfig" to mutableMapOf<String, Any>("cachePolicy" to CacheHelper.NO_CACHE)
            )
        val resultrepo = rrf.createRepository(reponame, configData)
        // val document = resultrepo.getDocument(reponame)
        Assertions.assertThat(resultrepo.getDocument(reponame))
            .isInstanceOf(Document::class.java).isNotNull
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

    private fun `setup wiremock to test getDocument`() {
        wms = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wms.start()
        WireMock.configureFor("localhost", wms.port())
        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("/$reponame")).willReturn(
                WireMock.aResponse().withBodyFile(
                    reponame
                )
            )
        )
        testURL = "http://localhost:${wms.port()}/"
    }
}
