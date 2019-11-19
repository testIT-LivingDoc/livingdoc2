package org.livingdoc.repositories.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input

@ExecutableDocument("local://RestRepositoryIntegrationTest.md")
class RestRepositoryIntegrationTest {

    @DecisionTableFixture
    class FileRepositoryDecisionTableFixture {

        private lateinit var cut: RESTRepository
        lateinit var wms: WireMockServer

        @Input("Host")
        private var host: String = ""

        @Input("Port")
        private var port: Int = 8080

        @Input("Path")
        private var path: String = ""

        @Input("File-Path")
        private var filePath: String = ""

        // Input is not yet initialized when using @beforeRow,
        // so this function has to be manually called before every test.
        private fun beforeCheck() {
            val restRepositoryConfig = RESTRepositoryConfig()
            restRepositoryConfig.baseURL = "$host:$port"
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

        @AfterEach
        fun afterRow() {
            wms.stop()
        }

        @Check("Throws RestDocumentNotFoundException")
        fun checkOutput(expectedValue: Boolean) {
            this.beforeCheck()

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
}
