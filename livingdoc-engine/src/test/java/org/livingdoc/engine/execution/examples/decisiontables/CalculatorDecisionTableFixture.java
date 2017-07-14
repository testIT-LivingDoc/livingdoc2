package org.livingdoc.engine.execution.examples.decisiontables;

import static org.assertj.core.api.Assertions.assertThat;

import org.livingdoc.api.fixtures.decisiontables.BeforeRow;
import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.Input;


public class CalculatorDecisionTableFixture {

    Calculator sut;

    @BeforeRow
    void beforeRow() {
        sut = new Calculator();
    }

    @Input("a")
    Float valueA;
    Float valueB;

    @Input("b")
    void setValueB(Float valueB) {
        this.valueB = valueB;
    }

    @Check("a + b = ?")
    void checkSum(Float expectedValue) {
        Float result = sut.sum(valueA, valueB);
        assertThat(result).isEqualTo(expectedValue);
    }

    @Check("a - b = ?")
    void checkDiff(Float expectedValue) {
        Float result = sut.diff(valueA, valueB);
        assertThat(result).isEqualTo(expectedValue);
    }

    @Check("a * b = ?")
    void checkMultiply(Float expectedValue) {
        Float result = sut.multiply(valueA, valueB);
        assertThat(result).isEqualTo(expectedValue);
    }

    @Check("a / b = ?")
    void checkDivide(Float expectedValue) {
        Float result = sut.divide(valueA, valueB);
        assertThat(result).isEqualTo(expectedValue);
    }

    private static class Calculator {

        private Float sum(Float a, Float b) {
            return a + b;
        }

        private Float diff(Float a, Float b) {
            return a - b;
        }

        private Float multiply(Float a, Float b) {
            return a * b;
        }

        private Float divide(Float a, Float b) {
            return a / b;
        }

    }

}
