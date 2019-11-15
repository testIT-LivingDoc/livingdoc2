package org.livingdoc.repositories.rest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

import org.assertj.core.api.Assertions.*
import org.livingdoc.repositories.model.decisiontable.DecisionTable

internal class RESTRepositoryTest {

    @Test
    fun `exception is thrown if document could not be found`() {

    val cut = RESTRepository("test", RESTRepositoryConfig())
        assertThrows<RESTDocumentNotFoundException> {
            cut.getDocument("foo-bar.html")
        }
    }

    @Test
    fun `mocked server testing`() {

        // starting server
        val wms = WireMockServer(options().dynamicHttpsPort().dynamicPort())
        wms.start()
        configureFor("localhost", wms.port())

        // setting REST Repository
        val cfg = RESTRepositoryConfig()
        cfg.baseURL = "http://localhost:${wms.port()}/"
        val cut = RESTRepository("test", cfg)

        wms.stubFor(
            get(urlEqualTo("/TTT/Testing.html")).willReturn(
                aResponse().withBodyFile(
                    "Testing.html"
                )
            )
        )

        // getting document and running asserts
        val doc = cut.getDocument("TTT/Testing.html")
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
        wms.verify(getRequestedFor(urlEqualTo("/TTT/Testing.html")))
        // stopping server
        wms.stop()
    }
}
