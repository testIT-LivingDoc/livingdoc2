package org.livingdoc.repositories.confluence

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ConfluenceRepositoryTest {
    @Test
    fun `exception is thrown if document could not be found`() {
        val cut = ConfluenceRepository("test", ConfluenceRepositoryConfig("", "", "", ""))
        assertThrows<ConfluenceDocumentNotFoundException> {
            cut.getDocument("31642164")
        }
    }

    @Disabled
    @Test
    fun `mocked server testing`() {

        // starting server
        val wms = WireMockServer(options().dynamicHttpsPort().dynamicPort())
        wms.start()
        configureFor("localhost", wms.port())

        // setting REST Repository
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig("http://localhost:8090/", "", "api", "test")
        )

        // getting document and running asserts
        val doc = cut.getDocument("327693")
        assertThat(doc.elements).isNotEmpty
        // verifying
        // TODO
        // stopping server
        wms.stop()
    }
}
