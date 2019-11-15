package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
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
        wms = WireMockServer(8080)
        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("/Testing.html")).willReturn(
                WireMock.aResponse().withBodyFile(
                    "Testing.html"
                )
            )
        )
        wms.start()
    }

    @Test
    fun `create RESTRepository`() {
        val comparisonRepository = RESTRepository(reponame, RESTRepositoryConfig())
        Assertions.assertThat(comparisonRepository.getDocument(reponame)).isNotNull

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/Testing.html")))
    }

    @Test
    fun `create REST repository via Factory`() {
        // testing factory
        val configData: Map<String, Any> = mutableMapOf<String, Any>("baseURL" to "http://localhost:8080/")
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
        val configData: Map<String, Any> = mutableMapOf<String, Any>("baseURL" to "http://localhost:8080/")
        val resultrepo = rrf.createRepository(reponame, configData)

        // manually created repository
        val comparisonRepository = RESTRepository(reponame, RESTRepositoryConfig())

        // comparison tests
        Assertions.assertThat(resultrepo.getDocument(reponame)).isNotNull
        Assertions.assertThat(comparisonRepository.getDocument(reponame)).isNotNull
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
