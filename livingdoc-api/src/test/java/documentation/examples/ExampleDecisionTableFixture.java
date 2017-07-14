package documentation.examples;

import java.util.function.Predicate;

import org.livingdoc.api.fixtures.decisiontables.AfterRow;
import org.livingdoc.api.fixtures.decisiontables.AfterTable;
import org.livingdoc.api.fixtures.decisiontables.BeforeFirstCheck;
import org.livingdoc.api.fixtures.decisiontables.BeforeRow;
import org.livingdoc.api.fixtures.decisiontables.BeforeTable;
import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture;
import org.livingdoc.api.fixtures.decisiontables.Input;


@DecisionTableFixture("Example")
@DecisionTableFixture({ "Beispiel", "Ejemplo" })
public class ExampleDecisionTableFixture {

    static Calculator sut;

    @BeforeTable
    static void beforeTable() {
        sut = new Calculator();
    }

    @BeforeRow
    void beforeRow() {

    }

    Long firstInput;

    @Input("second")
    @Input({ "zweiter", "segundo" })
    Long secondInput;

    @Input("first")
    @Input({ "erste", "primero" })
    void setFirstInput(Long value) {
        this.firstInput = value;
    }

    @BeforeFirstCheck
    void beforeFirstCheck() {

    }

    @Check("added?")
    @Check({ "addiert?", "adicional?" })
    void checkAddition(Predicate<Long> expectation) {
        long result = sut.add(firstInput, secondInput);
        //assertThat(result).as("actual: " + result).matches(expectation);
    }

    @AfterRow
    void afterRow() {

    }

    @AfterTable
    static void afterTable() {

    }

    private static class Calculator {

        long add(long a, long b) {
            return a + b;
        }

    }
}
