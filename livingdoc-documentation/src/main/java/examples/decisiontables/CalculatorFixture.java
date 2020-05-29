package examples.decisiontables;

import implementations.Calculator;
import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture;
import org.livingdoc.api.fixtures.decisiontables.Input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

/**
 * This example demonstrates a decision table fixture and its annotation possibility. Other than in the sample
 * we have no executable document.
 */

@DecisionTableFixture(value ="Calculator", parallel = true)
public class CalculatorFixture {

    Calculator sut = new Calculator();

    @Input("value a")
    Double valueA;
    @Input("value b")
    Double valueB;

    @Check("a + b = ?")
    void checkSum(Double expectedValue) {
        double actualValue = sut.sum(valueA, valueB);
        assertThat(actualValue).isEqualTo(expectedValue, offset(0.000000001d));
    }

    @Check("a - b = ?")
    void checkDiff(Double expectedValue) {
        double actualValue = sut.diff(valueA, valueB);
        assertThat(actualValue).isEqualTo(expectedValue, offset(0.000000001d));
    }

}
