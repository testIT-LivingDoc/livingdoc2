package org.livingdoc.converters.collection

import kotlin.reflect.KClass

open class SetConverter : AbstractCollectionConverter<Set<*>>() {

    override fun convertToTarget(collection: List<*>) = collection.toSet()

    override fun canConvertTo(targetType: KClass<*>) = Set::class == targetType
}
