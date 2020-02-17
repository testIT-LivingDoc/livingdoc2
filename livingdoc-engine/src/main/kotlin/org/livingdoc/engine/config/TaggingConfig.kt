package org.livingdoc.engine.config

import org.livingdoc.config.ConfigProvider

data class TaggingConfig(var tags: TaggingDefinition = TaggingDefinition()) {
    companion object {
        fun from(configProvider: ConfigProvider): TaggingConfig {
            return configProvider.getConfigAs("tags", TaggingConfig::class)
        }
    }
}
