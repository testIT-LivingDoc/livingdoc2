package org.livingdoc.repositories.rest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

internal class RESTRepositoryTest {

    val cut = RESTRepository("test", RESTRepositoryConfig())

    @Test
    fun `exception is thrown if document could not be found`() {
        assertThrows<RESTDocumentNotFoundException> {
            cut.getDocument("foo-bar.html")
        }
    }

    @Test
    fun `mocked server testing`() {

        // starting server
        val wms = WireMockServer(8080)
        wms.stubFor(
            get(urlEqualTo("/Testing.html")).willReturn(
                aResponse().withBodyFile(
                    "Testing.html"
                )
            )
        )
        wms.start()

        // getting document
        cut.getDocument("Testing.html")

        // verifying
        wms.verify(getRequestedFor(urlEqualTo("/Testing.html")))
        // optional assertions
        // Assertions.assertEquals()

        // stopping server
        wms.stop()
    }
}
