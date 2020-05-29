package org.livingdoc.example

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.tagging.Tag

/**
 * Tests caching when using a rest api.
 */
@Group
class RestGroup {
    companion object {
        // host has to be the same url as the "baseURL" in the config
        private var host: String = "localhost"
        private var port: Int = 8080
        private var path: String = "/TestingCache.html"
        // filePath is the file to return
        private var filePath: String = "TestingCache.html"

        private var wms: WireMockServer = WireMockServer(port)

        @JvmStatic
        @Before
        fun before() {
            wms.start()
            WireMock.configureFor(host, wms.port())

            wms.stubFor(
                WireMock.get(WireMock.urlEqualTo("")).willReturn(
                    WireMock.aResponse()
                        .withBody("I am active")
                )
            )

            wms.stubFor(
                WireMock.get(WireMock.urlEqualTo(path)).willReturn(
                    WireMock.aResponse().withBodyFile(
                        filePath
                    )
                )
            )
        }

        @JvmStatic
        @After
        fun after() {
            wms.stop()
        }
    }

    @Tag("markdown")
    @ExecutableDocument("rest://TestingCache.html")
    class CalculatorDocumentMdRest {

        @DecisionTableFixture()
        class CalculatorDecisionTableFixture {

            private lateinit var sut: Calculator

            @Input("a")
            private var valueA: Float = 0f
            private var valueB: Float = 0f

            @BeforeRow
            fun beforeRow() {
                sut = Calculator()
            }

            @Input("b")
            fun setValueB(valueB: Float) {
                this.valueB = valueB
            }

            @Check("a + b = ?")
            fun checkSum(expectedValue: Float) {
                val result = sut.sum(valueA, valueB)
                assertThat(result).isEqualTo(expectedValue)
            }

            @Check("a - b = ?")
            fun checkDiff(expectedValue: Float) {
                val result = sut.diff(valueA, valueB)
                assertThat(result).isEqualTo(expectedValue)
            }

            @Check("a * b = ?")
            fun checkMultiply(expectedValue: Float) {
                val result = sut.multiply(valueA, valueB)
                assertThat(result).isEqualTo(expectedValue)
            }

            @Check("a / b = ?")
            fun checkDivide(expectedValue: Float) {
                val result = sut.divide(valueA, valueB)
                assertThat(result).isEqualTo(expectedValue)
            }
        }
    }
}
