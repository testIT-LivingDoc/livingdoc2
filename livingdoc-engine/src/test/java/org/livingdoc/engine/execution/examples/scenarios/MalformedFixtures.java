package org.livingdoc.engine.execution.examples.scenarios;

import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.api.fixtures.scenarios.After;
import org.livingdoc.api.fixtures.scenarios.Before;
import org.livingdoc.api.fixtures.scenarios.Step;


public class MalformedFixtures {

    public static class NoDefaultConstructor {

        public NoDefaultConstructor(String fooBar) {
        }

    }

    public static class BeforeWithParameter {

        @Before
        void before(String param) {
        }

    }

    public static class StaticBeforeMethod {

        @Before
        static void before() {
        }

    }

    public static class StepMethodsWithSameAlias {

        @Step("step")
        void method1() {
        }

        @Step("step")
        void method2() {
        }
    }

    public static class StaticStepMethod {

        @Step("step")
        static void step() {
        }

    }

    public static class MismatchingParameterNumber {

        @Step("step with {parameter}")
        void step() {
        }

    }

    public static class AfterWithParameter {

        @After
        void after(String param) {
        }

    }

    public static class StaticAfterMethod {

        @After
        static void after() {
        }

    }

}
