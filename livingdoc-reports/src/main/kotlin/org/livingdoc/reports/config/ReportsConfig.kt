package org.livingdoc.reports.config

import org.livingdoc.config.ConfigProvider

data class ReportsConfig(var reports: List<ReportDefinition> = emptyList()) {
    companion object {
        fun from(configProvider: ConfigProvider): ReportsConfig {
            return configProvider.getConfigAs("reports", ReportsConfig::class)
        }
    }
}
