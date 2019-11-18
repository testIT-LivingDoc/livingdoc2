package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.HttpClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.Document

class RESTRepositoryFactoryTest {

    lateinit var wms: WireMockServer
    val reponame = "Testing.html"
    val rrf: RESTRepositoryFactory = RESTRepositoryFactory()
    lateinit var testURL: String
    val htmlFileName = "Testing.html"

    @BeforeEach
    fun startWM() {
        // starting server
        wms = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wms.start()
        WireMock.configureFor("localhost", wms.port())
        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("/$reponame")).willReturn(
                WireMock.aResponse().withBodyFile(
                    htmlFileName
                )
            )
        )
        testURL = "http://localhost:${wms.port()}/"
    }

    @Test
    fun `exception is thrown if document could not be found`() {

        val cut = RESTRepository("test", RESTRepositoryConfig())
        assertThrows<RESTDocumentNotFoundException> {
            cut.getDocument("foo-bar.html")
        }
    }

    @Test
    fun `create repository - RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        val comparisonRepository = RESTRepository(reponame, restrepoCfg, HttpClient())
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
    fun `get Document - RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        val comparisonRepository = RESTRepository(reponame, restrepoCfg, HttpClient())

        // test retrieval of document
        Assertions.assertThat(comparisonRepository.getDocument(reponame)).isInstanceOf(Document::class.java)
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

    @Test
    fun `get document - RESTRepositoryFactory`() {
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        val resultrepo = rrf.createRepository(reponame, configData)
        val document = resultrepo.getDocument(reponame)

        Assertions.assertThat(resultrepo.getDocument(reponame))
            .isInstanceOf(Document::class.java).isNotNull

        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

}
