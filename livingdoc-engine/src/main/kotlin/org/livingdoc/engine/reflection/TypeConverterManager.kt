package org.livingdoc.engine.reflection

import org.livingdoc.fixture.api.converter.TypeConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass

internal object TypeConverterManager {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /** Remembers created [TypeConverter] instances for faster access to them. */
    private val cache: MutableMap<Class<out TypeConverter<out Any>>, TypeConverter<*>> = mutableMapOf()

    /** List of default [TypeConverter] instances loaded via Java's [ServiceLoader] API. */
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
     * Returns an instance of the given converter class. Instances are created lazily and then cached. Repeated invocations
     * of this method will always return the same instance.
     *
     * @param converterType the class of the type converter
     * @return the type converter instance for the given type
     */
    fun getInstance(converterType: KClass<out TypeConverter<out Any>>): TypeConverter<*> {
        return getInstance(converterType.java)
    }

    /**
     * Returns an instance of the given converter class. Instances are created lazily and then cached. Repeated invocations
     * of this method will always return the same instance.
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