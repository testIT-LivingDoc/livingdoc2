package org.livingdoc.engine.execution.examples.scenarios;

import org.livingdoc.api.fixtures.scenarios.After;
import org.livingdoc.api.fixtures.scenarios.Before;
import org.livingdoc.api.fixtures.scenarios.Step;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;


public class LifeCycleFixture {

    public static Callback callback = mockkJClass(Callback.class);

    @Before
    void before() {
        callback.before();
    }

    @Step("step1")
    void step1() {
        callback.step1();
    }

    @Step("step2")
    @Step("Alternate template for step2")
    void step2() {
        callback.step2();
    }

    @After
    void after() {
        callback.after();
    }

    public static void reset() {
        clearJMockk(callback);
    }

    public interface Callback {

        void before();

        void step1();

        void step2();

        void after();

    }

}
