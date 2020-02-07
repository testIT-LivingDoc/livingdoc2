package org.livingdoc.repositories.cache

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class CacheHelperTest {

    private val cut = CacheHelper

    @Test
    fun `test caching input stream`(@TempDir tempDir: Path) {
        val givenInputStream = ByteArrayInputStream(ByteArray(12))
        val givenPath = Paths.get(tempDir.toString(), "testFilePath")

        assertThat(File(givenPath.toString())).doesNotExist()

        cut.cacheInputStream(givenInputStream, givenPath)

        assertThat(File(givenPath.toString())).exists()
    }

    @Test
    fun `test reading from cached file`() {
        val tmpFile = File.createTempFile("cachedFile", null)

        val cachedInputStream = cut.getCacheInputStream(tmpFile.toPath())
        assertThat(cachedInputStream).isNotNull()
    }

    @Test
    fun `test checking existence of cached file`() {
        val tmpFile = File.createTempFile("cachedFile", null)

        assertThat(cut.isCached(tmpFile.toPath())).isTrue()
    }

    @Test
    fun `test checking non-existence of cached file`(@TempDir tempDir: Path) {
        assertThat(cut.isCached(Paths.get(tempDir.toString(), "thisfiledoesnotexist.xyz"))).isFalse()
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
