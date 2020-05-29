package org.livingdoc.config

import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Provider for all Configurations of LivingDoc. Use this Object to get the global configuration and then parse specific
 * parts of this config into typed config objects Using the [YamlUtils].
 */
class ConfigProvider(private val config: Map<String, Any>) {

    /**
     * Get the configuration with the [configurationName] from the ConfigProvider. The configuration is parsed into a
     * typed object given by the [type]. The [type] must have a top-level property with name [configurationName].
     */
    fun <T : Any> getConfigAs(configurationName: String, type: KClass<T>): T {
        val configurationProperty = config[configurationName] ?: return type.createInstance()
        return YamlUtils.toObject(mapOf(configurationName to configurationProperty), type)
    }

    /**
     * Get the raw configuration by [configurationName]
     */
    fun getConfig(configurationName: String) = Optional.ofNullable(config[configurationName])

    companion object {

        /**
         * Loads the configuration from the `livingdoc.yml` file on the classpath root. The file location can be changed
         * by the Java System Property `livingdoc.config`.
         */
        fun load(): ConfigProvider {
            val fileName = System.getProperty("livingdoc.config", "livingdoc.yml")
            return loadFromFile(fileName)
        }

        /**
         * Loads the configuration from the given [configFileName] from Classpath.
         */
        fun loadFromFile(configFileName: String): ConfigProvider {
            val configFile = ConfigProvider::class.java.classLoader.getResource(configFileName)
                ?: throw FileNotFoundException("File not found: $configFileName")
            val inputStream = configFile.openStream()
            return loadFromStream(inputStream)
        }

        /**
         * Loads the configuration from the given [InputStream].
         */
        fun loadFromStream(inputStream: InputStream) = ConfigProvider(YamlUtils.loadFromStream(inputStream))
    }
}
