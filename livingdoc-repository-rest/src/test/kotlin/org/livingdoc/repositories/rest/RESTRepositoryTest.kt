package org.livingdoc.repositories.rest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.HttpClient
import org.assertj.core.api.Assertions

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

internal class RESTRepositoryTest {
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
    fun `Test file content - RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        val comparisonRepository = RESTRepository(reponame, restrepoCfg, HttpClient())
        val document = comparisonRepository.getDocument(reponame)
        val scenario = document.elements[2] as Scenario

        // Scenario Testing
        Assertions.assertThat(scenario).isInstanceOf(Scenario::class.java)

        Assertions.assertThat(scenario.steps).isNotNull
        Assertions.assertThat(scenario.steps).hasSize(5)
        Assertions.assertThat(scenario.steps[0].value).isEqualTo("First list item")
        Assertions.assertThat(scenario.steps[1].value).isEqualTo("Second list item")
        Assertions.assertThat(scenario.steps[2].value).isEqualTo("Third list item")
        Assertions.assertThat(scenario.steps[3].value).isEqualTo("Fourth list item")
        Assertions.assertThat(scenario.steps[4].value).isEqualTo("Fifth list item")

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

    @Test
    fun `Test content - RESTRepositoryFactory`() {
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        val resultrepo = rrf.createRepository(reponame, configData)
        val document = resultrepo.getDocument(reponame)

        // table testing
        val decisionTable = document.elements[1] as DecisionTable

        Assertions.assertThat(decisionTable).isInstanceOf(DecisionTable::class.java)

        Assertions.assertThat(decisionTable.headers).extracting("name")
            .containsExactly("BankAccount", "Balance", "Lastlogin")
        Assertions.assertThat(decisionTable.rows[0].headerToField.map { it.value.value })
            .containsExactly("104812731", "10293", "12.12.1212")
        Assertions.assertThat(decisionTable.rows[1].headerToField.map { it.value.value })
            .containsExactly("1048121231", "95642", "12.11.1982a")

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }

    /**
     * manually created multiple repositories
     */
    @Test
    fun `create multiple repositories and compare test`() {

        // testing factory
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        val resultrepo = rrf.createRepository(reponame, configData)
        // testing create Repository
        Assertions.assertThat(rrf.createRepository(reponame, configData))
            .isInstanceOf(RESTRepository::class.java).isNotNull
        val doc1 = resultrepo.getDocument(reponame)

        // manually created repository
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        val comparisonRepository = RESTRepository(reponame, restrepoCfg)
        Assertions.assertThat(RESTRepository(reponame, restrepoCfg))
            .isInstanceOf(RESTRepository::class.java).isNotNull
        val doc2 = comparisonRepository.getDocument(reponame)

        // comparison test of the two documents
        Assertions.assertThat(doc1.elements[0] as DecisionTable).isEqualTo(doc2.elements[0] as DecisionTable)
        Assertions.assertThat(doc1.elements[1] as DecisionTable).isEqualTo(doc2.elements[1] as DecisionTable)
        Assertions.assertThat(doc1.elements[2] as Scenario).isEqualTo(doc2.elements[2] as Scenario)
        Assertions.assertThat(resultrepo.getDocument(reponame).elements.size)
            .isEqualTo(comparisonRepository.getDocument(reponame).elements.size)

        // verification
        wms.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/$reponame")))
    }



    @Test
    fun `mocked server testing`() {

        // setting REST Repository
        // configure variables
        val cfg = RESTRepositoryConfig()
        cfg.baseURL = "http://localhost:${wms.port()}/"
        val cut = RESTRepository("test", cfg)
        val documentURL = "/TTT/Testing.html"
        val hostedHtmlFile = "Testing.html"

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
