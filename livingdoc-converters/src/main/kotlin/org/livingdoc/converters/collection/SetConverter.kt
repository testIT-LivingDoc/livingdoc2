package org.livingdoc.converters.collection

open class SetConverter : AbstractCollectionConverter<Set<Any>>() {

    override fun convertToTarget(collection: List<Any>) = collection.toSet()

    override fun canConvertTo(targetType: Class<*>?) = Set::class.java == targetType
}
