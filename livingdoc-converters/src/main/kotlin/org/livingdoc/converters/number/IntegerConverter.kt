package org.livingdoc.converters.number

import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * This converter converts a BigDecimal number to an Integer
 */
open class IntegerConverter : AbstractNumberConverter<Int>() {

    override val lowerBound: Int = Int.MIN_VALUE
    override val upperBound: Int = Int.MAX_VALUE

    /**
     * This function returns the value of number as an Integer
     */
    override fun convertToTarget(number: BigDecimal): Int = number.toInt()

    override fun canConvertTo(targetType: KClass<*>): Boolean {
        return Int::class == targetType
    }
}
