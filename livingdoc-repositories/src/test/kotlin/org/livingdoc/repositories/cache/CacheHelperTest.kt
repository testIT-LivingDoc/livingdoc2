package org.livingdoc.repositories.cache

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class CacheHelperTest {

    private val cut = CacheHelper

    @Test
    fun `test caching input stream`(@TempDir tempDir: Path) {
        val testStreamString = "testStream"
        val givenInputStream = testStreamString.byteInputStream()
        val givenPath = tempDir.resolve("testFilePath")

        assertThat(givenPath).doesNotExist()

        cut.cacheInputStream(givenInputStream, givenPath)

        assertThat(givenPath).exists()
        assertThat(givenPath).hasContent(testStreamString)
    }

    @Test
    fun `test reading from cached file`(@TempDir tempDir: Path) {
        val tmpFile = Files.createTempFile(tempDir, "cachedFile", null)

        val cachedInputStream = cut.getCacheInputStream(tmpFile)
        assertThat(cachedInputStream).isNotNull()
    }

    @Test
    fun `test checking existence of cached file`(@TempDir tempDir: Path) {
        val tmpFile = Files.createTempFile(tempDir, "cachedFile", null)

        assertThat(cut.isCached(tmpFile)).isTrue()
    }

    @Test
    fun `test checking non-existence of cached file`(@TempDir tempDir: Path) {
        assertThat(cut.isCached(tempDir.resolve("thisfiledoesnotexist.xyz"))).isFalse()
    }

    @Test
    fun `test checking for active internet connection`() {
        val wms = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wms.start()
        WireMock.configureFor("localhost", wms.port())

        wms.stubFor(
            WireMock.get(WireMock.urlEqualTo("")).willReturn(
                WireMock.aResponse()
                    .withBody("I am active")
            )
        )
        assertThat(cut.hasActiveNetwork(wms.baseUrl())).isTrue()

        wms.stop()
    }

    @Test
    fun `test checking for non existing internet connection`() {
        assertThat(cut.hasActiveNetwork("http://localhost:82838")).isFalse()
    }
}
