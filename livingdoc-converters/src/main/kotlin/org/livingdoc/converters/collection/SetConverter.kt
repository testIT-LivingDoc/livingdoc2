package org.livingdoc.converters.collection

open class SetConverter : AbstractCollectionConverter<Set<Any>>() {

    override fun convertToTarget(collection: List<Any>): Set<Any> {
        return collection.toSet()
    }

    override fun canConvertTo(targetType: Class<*>?): Boolean {
        val isJavaObjectType = Set::class.java == targetType
        val isKotlinType = Set::class == targetType
        return isJavaObjectType || isKotlinType
    }
}
