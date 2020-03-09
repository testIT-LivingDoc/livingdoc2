package org.livingdoc.converters.number

import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * This converter "converts" a BigDecimal to a BigDecimal
 */
open class BigDecimalConverter : AbstractNumberConverter<BigDecimal>() {

    override val lowerBound: BigDecimal? = null
    override val upperBound: BigDecimal? = null

    /**
     * This function returns the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be returned
     */
    override fun convertToTarget(number: BigDecimal): BigDecimal = number

    override fun canConvertTo(targetType: KClass<*>) = BigDecimal::class == targetType
}
