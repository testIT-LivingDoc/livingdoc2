package org.livingdoc.fixture.converter.number

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat


open class BigIntegerConverter : AbstractNumberConverter<BigInteger>() {

    override fun preProcess(value: String): String {
        // Fix for Java's DecimalFormat parser:
        //  - only when parsing BigDecimal targets
        //  - BigDecimal values containing "E+" will be cut of at the "+"
        //  - this can be prevented by pre-emotively replacing "E+" with "E"
        return value.replaceFirst("E+", "E", true)
    }

    override fun configureFormat(format: DecimalFormat) {
        format.isParseBigDecimal = true
    }

    override fun convertToTarget(number: Number): BigInteger {
        val bigDecimal = number as BigDecimal
        return bigDecimal.toBigInteger()
    }

}
