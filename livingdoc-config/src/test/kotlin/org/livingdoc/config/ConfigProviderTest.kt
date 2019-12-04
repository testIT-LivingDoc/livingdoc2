package org.livingdoc.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ConfigProviderTest {
    private val configFile = "test-config.yaml"

    @Test
    fun `config file can be loaded successfully`() {
        val config = ConfigProvider.loadFromFile(configFile)
        assertThat(config.getConfig("list")).isPresent
    }

    @Test
    fun `config can be parsed into typed object`() {
        val config = ConfigProvider.loadFromFile(configFile)
        config.getConfigAs("list", ConfigList::class)
    }

    data class ConfigList(var list: List<Entry> = emptyList())
    data class Entry(
        var name: String = "",
        var value: Int = 0
    )

    @Test
    fun `typed config must contain property with name of configuration`() {
        val config = ConfigProvider.loadFromFile(configFile)
        assertThrows<RuntimeException> { config.getConfigAs("list", ConfigBad::class) }
    }

    data class ConfigBad(val badProperty: String)

    @Test
    fun `fail to parse config with unexpected yaml properties`() {
        val config = ConfigProvider.loadFromFile(configFile)
        assertThrows<RuntimeException> { config.getConfigAs("fail", ConfigFail::class) }
    }

    data class ConfigFail(var fail: ConfigFailInner)
    data class ConfigFailInner(var test1: Any)

    @Test
    fun `return default typed config object if not given in yaml`() {
        val config = ConfigProvider.loadFromFile(configFile)
        config.getConfigAs("default", ConfigDefault::class)
    }

    data class ConfigDefault(var default: List<ComplexType> = emptyList())
    data class ComplexType(var someValue: String = "important")
}
