package org.livingdoc.engine.config

import org.livingdoc.config.ConfigProvider

data class TaggingConfig(var include: List<String> = emptyList(), var exclude: List<String> = emptyList()) {
    companion object {
        fun from(configProvider: ConfigProvider): TaggingConfig {
            return configProvider.getConfigAs("tags", TaggingConfig::class)
        }
    }
}
