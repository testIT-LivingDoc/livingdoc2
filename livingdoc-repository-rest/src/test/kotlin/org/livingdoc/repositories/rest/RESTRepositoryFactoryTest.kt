package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RESTRepositoryFactoryTest {

    /**
     * manually created multiple repositories
     */
    @Test
    fun `create multiple repositories and compare test`() {

        // starting server
        val wms = WireMockServer(8080)
        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("/Testing.html")).willReturn(
                WireMock.aResponse().withBodyFile(
                    "Testing.html"
                )
            )
        )
        wms.start()

        // testing factory
        val reponame = "Testing.html"
        val rrf: RESTRepositoryFactory
        rrf = RESTRepositoryFactory()
        val configData: Map<String, Any> = mutableMapOf<String, Any>("baseURL" to "http://localhost:8080/")
        val resultrepo = rrf.createRepository(reponame, configData)

        // manually created repository
        val comparisonRepository = RESTRepository(reponame, RESTRepositoryConfig())

        // comparison tests
        Assertions.assertThat(resultrepo.getDocument(reponame).elements.size).isEqualTo(comparisonRepository.getDocument(reponame).elements.size)

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/Testing.html")))

        // stopping server
        wms.stop()
    }
}
