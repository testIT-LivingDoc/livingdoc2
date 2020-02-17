package org.livingdoc.engine.config

import org.livingdoc.config.ConfigProvider

data class TaggingConfig(var tags: TaggingDefinition = TaggingDefinition()) {
    companion object {
        fun from(configProvider: ConfigProvider): TaggingConfig {
            return configProvider.getConfigAs("tags", TaggingConfig::class)
        }
    }

    val includedTags: List<String>
        get() = System.getProperty("livingdoc.tags.include")?.split(',') ?: tags.include
    val excludedTags: List<String>
        get() = System.getProperty("livingdoc.tags.exclude")?.split(',') ?: tags.exclude
}
