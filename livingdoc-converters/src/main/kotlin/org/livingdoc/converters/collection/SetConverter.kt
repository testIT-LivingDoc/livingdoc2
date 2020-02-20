package org.livingdoc.converters.collection

open class SetConverter : AbstractCollectionConverter<Set<*>>() {

    override fun convertToTarget(collection: List<*>) = collection.toSet()

    override fun canConvertTo(targetType: Class<*>) = Set::class.java == targetType
}
