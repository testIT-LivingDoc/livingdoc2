package org.livingdoc.converters.collection

import kotlin.reflect.KClass

open class ListConverter : AbstractCollectionConverter<List<*>>() {

    override fun convertToTarget(collection: List<*>): List<*> {
        return collection
    }

    override fun canConvertTo(targetType: KClass<*>) = List::class == targetType
}
