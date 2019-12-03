package examples.scenarios;

import static org.assertj.core.api.Assertions.assertThat;

import implementations.Calculator;
import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.fixtures.scenarios.*;

@ScenarioFixture("Calculator")
public class CalculatorFixture {

    private Calculator sut;

    @Before
    private void before(){
        sut = new Calculator();
    }

    @Step("adding {a} and {b} equals {c}")
    void add(@Binding("a") Float a, @Binding("b") Float b, @Binding("c") Float c) {
        double result = sut.sum(a, b);
        assertThat(result).isEqualTo(c);
    }

    @After
    private void after(){

    }

}
