package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.Input

internal class EmptyFixture

internal class CalculatorFixture {
    @Input("a")
    private var headerA: Float = 0.0f
    private var headerB: Float = 0.0f

    @Input("b")
    fun setB(valueB: Float) {
        this.headerB = valueB
    }

    @Check("a + b = ?")
    fun add() {}
}
