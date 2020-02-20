package org.livingdoc.converters.collection

open class ListConverter : AbstractCollectionConverter<List<*>>() {

    override fun convertToTarget(collection: List<*>): List<*> {
        return collection
    }

    override fun canConvertTo(targetType: Class<*>) = List::class.java == targetType
}
