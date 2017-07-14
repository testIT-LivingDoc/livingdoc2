package org.livingdoc.engine.fixtures

import org.livingdoc.api.conversion.TypeConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass


/**
 * This object manages [TypeConverter] instances. It has two responsibilities:
 *
 * 1. load and manage default type converters provided by LivingDoc
 * 2. create and cache instances of (custom) type converters
 */
internal object TypeConverterManager {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val cache: MutableMap<Class<out TypeConverter<out Any>>, TypeConverter<*>> = mutableMapOf()
    private val defaultConverters: List<TypeConverter<*>>

    init {
        log.info("loading default type converters...")
        defaultConverters = ServiceLoader.load(TypeConverter::class.java).toList()
        defaultConverters.forEach {
            log.debug("loaded default type converter: {}", it.javaClass.canonicalName)
            cache.put(it.javaClass, it)
        }
    }

    /**
     * Returns an instance of the given [TypeConverter] class. Instances are created lazily and then cached. Repeated
     * invocations of this method will always return the same instance.
     *
     * @param converterType the class of the type converter
     * @return the type converter instance for the given type
     */
    fun getInstance(converterType: KClass<out TypeConverter<out Any>>): TypeConverter<*> {
        return getInstance(converterType.java)
    }

    /**
     * Returns an instance of the given [TypeConverter] class. Instances are created lazily and then cached. Repeated
     * invocations of this method will always return the same instance.
     *
     * @param converterType the class of the type converter
     * @return the type converter instance for the given type
     */
    fun getInstance(converterType: Class<out TypeConverter<out Any>>): TypeConverter<*> {
        return cache.computeIfAbsent(converterType, {
            log.debug("creating new cached instance of {}", it.canonicalName)
            it.newInstance()
        })
    }

    /**
     * Returns the list of LivingDoc's default type converters.
     */
    fun getDefaultConverters(): List<TypeConverter<*>> {
        return defaultConverters
    }

}