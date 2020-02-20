package org.livingdoc.converters

import java.util.*
import kotlin.reflect.KClass
import org.livingdoc.api.conversion.TypeConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This object manages [TypeConverter] instances. It has two responsibilities:
 *
 * 1. load and manage default type converters provided by LivingDoc
 * 2. create and cache instances of (custom) type converters
 */
object TypeConverterManager {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val cache: MutableMap<Class<out TypeConverter<*>>, TypeConverter<*>> = mutableMapOf()
    private val defaultConverters: List<TypeConverter<*>>

    init {
        log.info("loading default type converters...")
        defaultConverters = ServiceLoader.load(TypeConverter::class.java).toList()
        defaultConverters.forEach { converter ->
            log.debug("loaded default type converter: {}", converter.javaClass.canonicalName)
            cache[converter.javaClass] = converter
        }
    }

    /**
     * Returns an instance of the given [TypeConverter] class. Instances are created lazily and then cached. Repeated
     * invocations of this method will always return the same instance.
     *
     * @param converterType the class of the type converter
     * @return the type converter instance for the given type
     */
    fun getInstance(converterType: KClass<out TypeConverter<*>>): TypeConverter<*> {
        return getInstance(converterType.java)
    }

    /**
     * Returns an instance of the given [TypeConverter] class. Instances are created lazily and then cached. Repeated
     * invocations of this method will always return the same instance.
     *
     * @param converterType the class of the type converter
     * @return the type converter instance for the given type
     */
    private fun getInstance(converterType: Class<out TypeConverter<*>>): TypeConverter<*> {
        return cache.computeIfAbsent(converterType) { clazz ->
            log.debug("creating new cached instance of {}", clazz.canonicalName)
            clazz.getDeclaredConstructor().newInstance()
        }
    }

    /**
     * Returns the list of LivingDoc's default type converters.
     */
    fun getDefaultConverters(): List<TypeConverter<*>> {
        return defaultConverters
    }
}
