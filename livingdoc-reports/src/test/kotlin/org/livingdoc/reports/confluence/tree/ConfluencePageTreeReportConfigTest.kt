package org.livingdoc.reports.confluence.tree

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.livingdoc.config.YamlUtils

internal class ConfluencePageTreeReportConfigTest {
    @Test
    fun `can parse config`() {
        val parsedConfig = YamlUtils.toObject(mapOf(
            "rootContentId" to 27,
            "baseURL" to "https://internal.example.com/",
            "username" to "livingdoc-reports",
            "password" to "secure!p4ssw0rd",
            "path" to "/confluence",
            "comment" to "Jenkins from Staging"
        ), ConfluencePageTreeReportConfig::class)

        assertThat(parsedConfig).isEqualTo(
            ConfluencePageTreeReportConfig(
                27,
                "https://internal.example.com/",
                "/confluence",
                "livingdoc-reports",
                "secure!p4ssw0rd",
                "Jenkins from Staging"
            )
        )
    }
}
