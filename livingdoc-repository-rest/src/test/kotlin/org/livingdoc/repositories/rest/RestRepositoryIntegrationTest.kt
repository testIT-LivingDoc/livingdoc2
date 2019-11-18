package org.livingdoc.repositories.rest

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.livingdoc.api.fixtures.scenarios.Before

var wms: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

@Before
fun before() {
    wms.start()
    wms.stubFor(
        WireMock.get(WireMock.urlEqualTo("/RestRepositoryIntegrationTest.md")).willReturn(
            WireMock.aResponse().withBodyFile(
                "htmlFileName"
            )
        ))
}

@ExecutableDocument("local://RestRepositoryIntegrationTest.md")
class RestRepositoryIntegrationTest {

    @DecisionTableFixture
    class FileRepositoryDecisionTableFixture {

        private lateinit var cut: RESTRepository

        @Input("Rest-Url")
        private var restUrl: String = ""

        @BeforeRow
        fun beforeRow() {
            val restRepositoryConfig = RESTRepositoryConfig()
            restRepositoryConfig.baseURL = restUrl
            cut = RESTRepository("", restRepositoryConfig)
        }

        @Check("Throws RestDocumentNotFoundException")
        fun checkOutput(expectedValue: Boolean) {
            if (expectedValue) {
                assertThrows<RESTDocumentNotFoundException> {
                    cut.getDocument(restUrl)
                }
                return
            }
            assertDoesNotThrow {
                cut.getDocument(restUrl)
            }
        }
    }
}
