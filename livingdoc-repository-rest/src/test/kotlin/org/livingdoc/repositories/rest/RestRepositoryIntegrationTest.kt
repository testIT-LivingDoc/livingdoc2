package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.AfterRow
import org.livingdoc.api.fixtures.decisiontables.BeforeFirstCheck
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.cache.CacheHelper
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

@ExecutableDocument("local://RestRepositoryIntegrationTest.md")
class RestRepositoryIntegrationTest {

    @DecisionTableFixture
    class RestRepositoryDecisionTableFixture {

        private lateinit var cut: RESTRepository
        private lateinit var wms: WireMockServer

        @Input("Host")
        private var host: String = ""

        @Input("Port")
        private var port: Int = 8080

        @Input("Path")
        private var path: String = ""

        @Input("File-Path")
        private var filePath: String = ""

        @BeforeFirstCheck
        private fun beforeCheck() {
            val restRepositoryConfig = RESTRepositoryConfig()
            restRepositoryConfig.baseURL = "$host:$port"
            restRepositoryConfig.cacheConfig.cachePolicy = CacheHelper.NO_CACHE

            cut = RESTRepository("", restRepositoryConfig)

            wms = WireMockServer(port)
            wms.start()
            WireMock.configureFor(host, wms.port())

            wms.stubFor(
                WireMock.get(WireMock.urlEqualTo(path)).willReturn(
                    WireMock.aResponse().withBodyFile(
                        filePath
                    )
                )
            )
        }

        @AfterRow
        fun afterRow() {
            wms.stop()
        }

        @Check("Throws RestDocumentNotFoundException")
        fun checkOutput(expectedValue: Boolean) {
            if (expectedValue) {
                assertThrows<RESTDocumentNotFoundException> {
                    cut.getDocument(path)
                }
                return
            }
            assertDoesNotThrow {
                cut.getDocument(path)
            }
        }
    }

    @ScenarioFixture
    class RestRepositoryScenarioFixture {

        private lateinit var doc: Document

        @Before
        fun before() {
            // Starting server
            val wms = WireMockServer(WireMockConfiguration.options().dynamicHttpsPort().dynamicPort())
            wms.start()
            WireMock.configureFor("localhost", wms.port())

            // Set REST Repository
            val cfg = RESTRepositoryConfig()
            cfg.baseURL = "http://localhost:${wms.port()}/"
            val cut = RESTRepository("test", cfg)

            wms.stubFor(
                WireMock.get(WireMock.urlEqualTo("/TTT/Testing.html")).willReturn(
                    WireMock.aResponse().withBodyFile(
                        "Testing.html"
                    )
                )
            )

            // Get document
            doc = cut.getDocument("TTT/Testing.html")

            // Stop wms
            wms.stop()
        }

        @Step("Example {exampleIndex} should {doesExist}")
        fun checkExampleExistence(
            @Binding("exampleIndex") exampleIndex: Int,
            @Binding("doesExist") doesExist: String
        ) {
            when (doesExist) {
                "exist" -> assertThat(doc.elements[exampleIndex]).isNotNull
                "not exist" -> assertThat(doc.elements.size).isLessThanOrEqualTo(exampleIndex)
            }
        }

        @Step("Example {exampleIndex} should be a {exampleType}")
        fun checkExampleType(
            @Binding("exampleIndex") exampleIndex: Int,
            @Binding("exampleType") exampleType: String
        ) {
            when (exampleType) {
                "DecisionTable" -> assertThat(doc.elements[exampleIndex]).isInstanceOf(DecisionTable::class.java)
                "Scenario" -> assertThat(doc.elements[exampleIndex]).isInstanceOf(Scenario::class.java)
            }
        }

        @Step("Example {exampleIndex} should have {nrRows} rows")
        fun checkNrRows(
            @Binding("exampleIndex") exampleIndex: Int,
            @Binding("nrRows") nrRows: Int
        ) {
            assertThat((doc.elements[exampleIndex] as DecisionTable).rows).hasSize(nrRows)
        }

        @Step("Row {rowIndex} of example {exampleIndex} should have {nrHeaders} headers")
        fun checkNrHeaders(
            @Binding("rowIndex") rowIndex: Int,
            @Binding("exampleIndex") exampleIndex: Int,
            @Binding("nrHeaders") nrHeaders: Int
        ) {
            assertThat((doc.elements[exampleIndex] as DecisionTable).rows[rowIndex].headerToField).hasSize(nrHeaders)
        }
    }
}
