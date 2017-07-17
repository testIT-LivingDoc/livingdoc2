package examples.decisiontables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture;
import org.livingdoc.api.fixtures.decisiontables.Input;

import implementations.Calculator;


@DecisionTableFixture("Calculator")
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
