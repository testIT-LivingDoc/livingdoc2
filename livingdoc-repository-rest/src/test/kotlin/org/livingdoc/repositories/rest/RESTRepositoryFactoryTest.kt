package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.client.HttpClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

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
    fun `create RESTRepository`() {
        val restrepoCfg = RESTRepositoryConfig()
        restrepoCfg.baseURL = testURL
        val comparisonRepository = RESTRepository(reponame, restrepoCfg, HttpClient())
        // testing constructor of Rest Repository
        Assertions.assertThat(RESTRepository(reponame, restrepoCfg))
            .isInstanceOf(RESTRepository::class.java).isNotNull

        // document related tests
        val document = comparisonRepository.getDocument(reponame)

        val scenario = document.elements[2] as Scenario

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
    fun `create REST repository via Factory`() {
        // testing factory
        val configData: Map<String, Any> =
            mutableMapOf<String, Any>("baseURL" to testURL)
        val resultrepo = rrf.createRepository(reponame, configData)
        // testing create Repository
        Assertions.assertThat(rrf.createRepository(reponame, configData))
            .isInstanceOf(RESTRepository::class.java).isNotNull

        // document related tests
        val document = resultrepo.getDocument(reponame)

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

    @AfterEach
    fun stopWM() {
        // stopping server
        wms.stop()
    }
}
