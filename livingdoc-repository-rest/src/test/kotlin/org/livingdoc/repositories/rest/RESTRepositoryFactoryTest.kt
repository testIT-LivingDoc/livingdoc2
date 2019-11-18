package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RESTRepositoryFactoryTest {

    lateinit var wms: WireMockServer
    val reponame = "Testing.html"
    val rrf: RESTRepositoryFactory = RESTRepositoryFactory()

    @BeforeEach
    fun startWM() {

        // starting server
        wms = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wms.start()
        WireMock.configureFor("localhost", wms.port())
        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("/Testing.html")).willReturn(
                WireMock.aResponse().withBodyFile(
                    "Testing.html"
                )
            )
        )
    }

    @Test
    fun `create RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = "http://localhost:${wms.port()}/"
        val comparisonRepository = RESTRepository(reponame, restrepoCfg)
        Assertions.assertThat(comparisonRepository.getDocument(reponame)).isNotNull

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/Testing.html")))
    }

    @Test
    fun `create REST repository via Factory`() {
        // testing factory
        val configData: Map<String, Any> =
                mutableMapOf<String, Any>("baseURL" to "http://localhost:${wms.port()}/")
        val resultrepo = rrf.createRepository(reponame, configData)

        Assertions.assertThat(resultrepo.getDocument(reponame)).isNotNull

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/Testing.html")))
    }

    /**
     * manually created multiple repositories
     */
    @Test
    fun `create multiple repositories and compare test`() {

        // testing factory
        val configData: Map<String, Any> =
                mutableMapOf<String, Any>("baseURL" to "http://localhost:${wms.port()}/")
        val resultrepo = rrf.createRepository(reponame, configData)
        val doc1 = resultrepo.getDocument(reponame)

        // manually created repository
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = "http://localhost:${wms.port()}/"
        val comparisonRepository = RESTRepository(reponame, restrepoCfg)
        val doc2 = comparisonRepository.getDocument(reponame)

        // comparison tests
        Assertions.assertThat(doc1).isNotNull
        Assertions.assertThat(doc2).isNotNull
        Assertions.assertThat(resultrepo.getDocument(reponame).elements.size).isEqualTo(comparisonRepository.getDocument(reponame).elements.size)

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/Testing.html")))
        }

    @AfterEach
    fun stopWM() {
        // stopping server
        wms.stop()
    }
}
