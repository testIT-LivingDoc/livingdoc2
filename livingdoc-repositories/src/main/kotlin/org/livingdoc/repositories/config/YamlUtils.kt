package org.livingdoc.repositories.config

import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import kotlin.reflect.KClass

object YamlUtils {

    private val yaml = Yaml()

    /**
     * Reads the configuration data from the given [InputStream]. The stream
     * needs to contain valid YAML data. After reading, the stream is closed.
     *
     * @param inputStream the stream to read
     * @return the read configuration data as a [Map]
     */
    fun loadFromStream(inputStream: InputStream): Map<String, Any> {
        return inputStream.use {
            @Suppress("UNCHECKED_CAST")
            yaml.loadAs(it, Map::class.java) as Map<String, Any>
        }
    }

    /**
     * Reads the configuration data from the given [InputStream] as an instance
     * of the given type. The stream needs to contain valid YAML data. After
     * reading, the stream is closed.
     *
     * @param inputStream the stream to read
     * @return the read configuration data as a [Map]
     */
    fun <T : Any> loadFromStreamAs(inputStream: InputStream, type: KClass<T>): T {
        return inputStream.use {
            @Suppress("UNCHECKED_CAST")
            yaml.loadAs(it, type.java)
        }
    }

    /**
     * Creates an instance of the given type based on the provided configData.
     *
     * If there is no data for a field of the given type, the type's default will be used.
     *
     * @param configData the raw data to be used when creating the instance
     * @param type the type of object to create
     * @return the created object instance
     */
    fun <T : Any> toObject(configData: Map<String, Any>, type: KClass<T>): T {
        // This is easier than implementing the function. But it is a workaround.
        // The Yaml class does not provide a function for this type of conversion.
        // TODO: maybe implement own function, or do it some other way.
        val yamlString = yaml.dump(configData)
        return yaml.loadAs(yamlString, type.java)
    }

}