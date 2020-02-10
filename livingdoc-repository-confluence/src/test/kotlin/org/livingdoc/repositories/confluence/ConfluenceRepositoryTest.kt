package org.livingdoc.repositories.confluence

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.livingdoc.repositories.cache.CacheConfiguration
import org.livingdoc.repositories.cache.CacheHelper
import org.livingdoc.repositories.cache.InvalidCachePolicyException
import java.nio.file.Files
import java.nio.file.Path

internal class ConfluenceRepositoryTest {
    @Test
    fun `exception is thrown if document could not be found`() {
        val cacheConfig = CacheConfiguration("", CacheHelper.NO_CACHE)
        val cut = ConfluenceRepository("test", ConfluenceRepositoryConfig("", "", "", "", cacheConfig))
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

    @Test
    fun `test parsing document from cache`(@TempDir tempDir: Path) {
        val tmpFile = Files.createTempFile(tempDir, "", null)
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig(
                "",
                "",
                "",
                "",
                CacheConfiguration(
                    tmpFile.parent.toString(),
                    CacheHelper.CACHE_ONCE
                )
            )
        )

        assertThat(tmpFile).exists()

        assertDoesNotThrow {
            cut.getDocument(tmpFile.fileName.toString())
        }
    }

    @Test
    fun `test thrown exception when getting non existent document from cache`() {
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig(
                "",
                "",
                "",
                "",
                CacheConfiguration(
                    "",
                    CacheHelper.CACHE_ALWAYS
                )
            )
        )

        val documentIdentifierNonExistent = "273933"
        assertThrows<ConfluenceDocumentNotFoundException> {
            cut.getDocument(documentIdentifierNonExistent)
        }
    }

    @Test
    fun `test thrown exception for invalid cache policy value`() {
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig(
                "",
                "",
                "",
                "",
                CacheConfiguration(
                    "",
                    "invalidCachePolicy"
                )
            )
        )

        val documentIdentifier = "273933"
        assertThrows<InvalidCachePolicyException> {
            cut.getDocument(documentIdentifier)
        }
    }

    @Test
    fun `test cache policy always without internet`(@TempDir tempDir: Path) {
        val tmpFile = Files.createTempFile(tempDir, "", null)
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig(
                "",
                "",
                "",
                "",
                CacheConfiguration(
                    tmpFile.parent.toString(),
                    CacheHelper.CACHE_ALWAYS
                )
            )
        )

        assertThat(tmpFile).exists()

        assertDoesNotThrow {
            cut.getDocument(tmpFile.fileName.toString())
        }
    }

    @Test
    fun `test cache policy no cache without internet`() {
        val cut = ConfluenceRepository(
            "test",
            ConfluenceRepositoryConfig(
                "",
                "",
                "",
                "",
                CacheConfiguration(
                    "",
                    CacheHelper.NO_CACHE
                )
            )
        )

        val documentIdentifier = "327693"
        assertThrows<ConfluenceDocumentNotFoundException> {
            cut.getDocument(documentIdentifier)
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
