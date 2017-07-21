package org.livingdoc.converters.collection

import java.lang.reflect.AnnotatedElement
import kotlin.reflect.KClass

internal abstract class CollectionConverterContract<T: Collection<Any>> {

    abstract val cut: AbstractCollectionConverter<T>
    abstract val collectionClass: Class<*>

    protected fun getParameterTypeConverter(fixtureClass: KClass<*>, methodName: String): AnnotatedElement? {
        val method = fixtureClass.java.getMethod(methodName, collectionClass)
        return method.parameters[0]
    }
}
