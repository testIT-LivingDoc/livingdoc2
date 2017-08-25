package org.livingdoc.converters.collection

open class ListConverter : AbstractCollectionConverter<List<Any>>() {

    override fun convertToTarget(collection: List<Any>): List<Any> {
        return collection
    }

    override fun canConvertTo(targetType: Class<*>?): Boolean {
        val isJavaObjectType = List::class.javaObjectType == targetType
        val isKotlinType = List::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
