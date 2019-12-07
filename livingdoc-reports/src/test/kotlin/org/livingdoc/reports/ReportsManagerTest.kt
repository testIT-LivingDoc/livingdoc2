package org.livingdoc.reports

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.config.ConfigProvider
import org.livingdoc.reports.config.ReportsConfig

internal class ReportsManagerTest {

    @Test
    fun `parse config correctly`() {
        val configYaml = """

            reports:
              - name: "default"
                format: "html"
                config:
                  foo: "test repository one"
                  bar: 1.11
              - name: "alt-1"
                format: "json"
                config:
                  foo: "test repository two"
                  bar: 2.22
        """.trimIndent()

        val configuration = readConfigFromString(configYaml)
        assertThat(configuration.reports).hasSize(2)
    }

    private fun readConfigFromString(config: String): ReportsConfig {
        return ReportsConfig.from(ConfigProvider.loadFromStream(config.byteInputStream()))
    }
}
