package org.livingdoc.repositories.config

import java.io.FileNotFoundException
import java.io.InputStream

data class Configuration(
        var repositories: List<RepositoryDefinition> = emptyList()
) {

    companion object {

        /**
         * Loads a [Configuration] instance from the `livingdoc.yml` file on the classpath root.
         */
        fun load(): Configuration {
            val configFile = Configuration::class.java.classLoader.getResource("livingdoc.yml")
                    ?: throw FileNotFoundException("File not found: livingdoc.yml")
            val inputStream = configFile.openStream()
            return loadFromStream(inputStream)
        }

        /**
         * Loads a [Configuration] instance from the given [InputStream].
         */
        fun loadFromStream(inputStream: InputStream): Configuration {
            return YamlUtils.loadFromStreamAs(inputStream, Configuration::class)
        }

    }

}
