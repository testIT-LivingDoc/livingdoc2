package org.livingdoc.config

import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Provider for all Configurations of LivingDoc. Use this Object to get the global configuration and then parse specific
 * parts of this config into typed config objects Using the [YamlUtils].
 */
object ConfigProvider {

    /**
     * Loads the configuration from the `livingdoc.yml` file on the classpath root.
     */
    fun load(): Map<String, Any> {
        return loadFromFile("livingdoc.yml")
    }

    /**
     * Loads the configuration from the given [configFile] from Classpath.
     */
    fun loadFromFile(configFileName: String): Map<String, Any> {
        val configFile = ConfigProvider::class.java.classLoader.getResource(configFileName)
            ?: throw FileNotFoundException("File not found: $configFileName")
        val inputStream = configFile.openStream()
        return loadFromStream(inputStream)
    }

    /**
     * Loads the configuration from the given [InputStream].
     */
    fun loadFromStream(inputStream: InputStream) = YamlUtils.loadFromStream(inputStream)
}
