package org.livingdoc.engine.config

import org.livingdoc.config.ConfigProvider

data class TaggingConfig(private val tags: TaggingDefinition = TaggingDefinition()) {
    companion object {
        fun from(configProvider: ConfigProvider): TaggingConfig {
            return configProvider.getConfigAs("tags", TaggingConfig::class)
        }
    }

    val includedTags = System.getProperty("livingdoc.tags.include")?.split(',') ?: tags.include
    val excludedTags = System.getProperty("livingdoc.tags.exclude")?.split(',') ?: tags.exclude
}
