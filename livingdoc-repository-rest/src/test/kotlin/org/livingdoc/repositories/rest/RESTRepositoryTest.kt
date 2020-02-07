package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.HttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.cache.CacheHelper
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

internal class RESTRepositoryTest {
    private lateinit var wms: WireMockServer
    private val repoName = "Testing.html"
    private val rrf: RESTRepositoryFactory = RESTRepositoryFactory()
    private lateinit var testURL: String
    private val htmlFileName = "Testing.html"

    @BeforeEach
    fun startWM() {
        // starting server
        wms = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wms.start()
        WireMock.configureFor("localhost", wms.port())
        wms.stubFor(
            WireMock.get(urlEqualTo("/$repoName")).willReturn(
                WireMock.aResponse().withBodyFile(
                    htmlFileName
                )
            )
        )
        testURL = "http://localhost:${wms.port()}/"
    }

    @Test
    fun `Test file content - RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        restrepoCfg.cacheConfig.cachePolicy = CacheHelper.NO_CACHE
        val comparisonRepository = RESTRepository(repoName, restrepoCfg, HttpClient())
        val document = comparisonRepository.getDocument(repoName)
        val scenario = document.elements[2] as Scenario

        // Scenario Testing
        assertThat(scenario).isInstanceOf(Scenario::class.java)

        assertThat(scenario.steps).isNotNull
        assertThat(scenario.steps).hasSize(5)
        assertThat(scenario.steps[0].value).isEqualTo("First list item")
        assertThat(scenario.steps[1].value).isEqualTo("Second list item")
        assertThat(scenario.steps[2].value).isEqualTo("Third list item")
        assertThat(scenario.steps[3].value).isEqualTo("Fourth list item")
        assertThat(scenario.steps[4].value).isEqualTo("Fifth list item")

        // verification
        wms.verify(getRequestedFor(urlEqualTo("/$repoName")))
    }

    @Test
    fun `Test content - RESTRepositoryFactory`() {
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        val resultrepo = rrf.createRepository(repoName, configData)
        val document = resultrepo.getDocument(repoName)

        // table testing
        val decisionTable = document.elements[1] as DecisionTable

        assertThat(decisionTable).isInstanceOf(DecisionTable::class.java)

        assertThat(decisionTable.headers).extracting("name")
            .containsExactly("BankAccount", "Balance", "Lastlogin")
        assertThat(decisionTable.rows[0].headerToField.map { it.value.value })
            .containsExactly("104812731", "10293", "12.12.1212")
        assertThat(decisionTable.rows[1].headerToField.map { it.value.value })
            .containsExactly("1048121231", "95642", "12.11.1982a")

        // verification
        wms.verify(getRequestedFor(urlEqualTo("/$repoName")))
    }

    /**
     * manually created multiple repositories
     */
    @Test
    fun `create multiple repositories and compare test`() {

        // testing factory
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>(
                "baseURL" to testURL,
                "cacheConfig" to mutableMapOf<String, Any>("cachePolicy" to CacheHelper.NO_CACHE)
            )
        val resultRepo = rrf.createRepository(repoName, configData)
        // testing create Repository
        assertThat(rrf.createRepository(repoName, configData))
            .isInstanceOf(RESTRepository::class.java).isNotNull
        val doc1 = resultRepo.getDocument(repoName)

        // manually created repository
        val restRepoCfg = RESTRepositoryConfig()
        restRepoCfg.baseURL = testURL
        restRepoCfg.cacheConfig.cachePolicy = CacheHelper.NO_CACHE
        val comparisonRepository = RESTRepository(repoName, restRepoCfg)
        assertThat(RESTRepository(repoName, restRepoCfg))
            .isInstanceOf(RESTRepository::class.java).isNotNull
        val doc2 = comparisonRepository.getDocument(repoName)

        // comparison test of the two documents
        assertThat(doc1.elements[0] as DecisionTable).isEqualTo(doc2.elements[0] as DecisionTable)
        assertThat(doc1.elements[1] as DecisionTable).isEqualTo(doc2.elements[1] as DecisionTable)
        assertThat(doc1.elements[2] as Scenario).isEqualTo(doc2.elements[2] as Scenario)
        assertThat(resultRepo.getDocument(repoName).elements.size)
            .isEqualTo(comparisonRepository.getDocument(repoName).elements.size)

        // verification
        wms.verify(getRequestedFor(urlEqualTo("/$repoName")))
    }

    @Test
    fun `mocked server testing`() {

        // setting REST Repository
        // configure variables
        val cfg = RESTRepositoryConfig()
        cfg.baseURL = "http://localhost:${wms.port()}/"
        cfg.cacheConfig.cachePolicy = CacheHelper.NO_CACHE
        val cut = RESTRepository("test", cfg)
        val documentURL = "/Testing.html"

        // getting document and running asserts
        val doc = cut.getDocument(documentURL)
        assertThat(doc.elements[0]).isNotNull

        val documentNode = doc.elements[0] as DecisionTable

        assertThat(documentNode)
            .isInstanceOf(DecisionTable::class.java)

        assertThat(documentNode.headers).extracting("name")
            .containsExactly("a", "b", "a + b = ?", "a - b = ?", "a * b = ?", "a / b = ?")

        assertThat(documentNode.rows).hasSize(2)
        assertThat(documentNode.rows[0].headerToField).hasSize(6)
        assertThat(documentNode.rows[1].headerToField).hasSize(6)

        assertThat(documentNode.rows[0].headerToField.map { it.value.value })
            .containsExactly("1", "1", "2", "0", "1", "1")
        assertThat(documentNode.rows[1].headerToField.map { it.key.name })
            .containsExactly("a", "b", "a + b = ?", "a - b = ?", "a * b = ?", "a / b = ?")
        assertThat(documentNode.rows[1].headerToField.map { it.value.value })
            .containsExactly("1", "0", "1", "1", "0", "Infinity")

        // verifying
        wms.verify(getRequestedFor(urlEqualTo(documentURL)))
    }

    @AfterEach
    fun stopWM() {
        // stopping server
        wms.stop()
    }
}
