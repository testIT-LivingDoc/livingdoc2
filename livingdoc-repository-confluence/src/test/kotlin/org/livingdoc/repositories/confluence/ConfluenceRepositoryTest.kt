package org.livingdoc.repositories.confluence

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import io.mockk.mockk
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

    @Test
    fun `test separation of identifier and version`() {
        val mockedConfig = mockk<ConfluenceRepositoryConfig>(relaxed = true)

        val cut = ConfluenceRepository("test", mockedConfig)

        val documentIdentifier = "327693@9"
        val versioned = cut.getDocumentIdAndVersion(documentIdentifier)
        assertThat(versioned).isNotNull
        assertThat(versioned.documentId).isEqualTo("327693")
        assertThat(versioned.documentVersion).isEqualTo(9)
    }

    @Test
    fun `test separation of identifier and version throws`() {
        val mockedConfig = mockk<ConfluenceRepositoryConfig>(relaxed = true)
        val cut = ConfluenceRepository("test", mockedConfig)

        val documentIdentifierMultipleVersions = "327693@9@6"
        assertThrows<ConfluenceDocumentNotFoundException> {
            cut.getDocumentIdAndVersion(documentIdentifierMultipleVersions)
        }

        val documentIdentifierNoVersions = "327693"
        assertThrows<ConfluenceDocumentNotFoundException> {
            cut.getDocumentIdAndVersion(documentIdentifierNoVersions)
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