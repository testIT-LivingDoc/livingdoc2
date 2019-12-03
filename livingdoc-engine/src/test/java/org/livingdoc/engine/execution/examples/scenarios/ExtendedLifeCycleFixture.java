package org.livingdoc.engine.execution.examples.scenarios;

import org.jetbrains.annotations.NotNull;
import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.fixtures.scenarios.Binding;
import org.livingdoc.api.fixtures.scenarios.Step;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;


public class ExtendedLifeCycleFixture {

    public static Callback callback = mockkJClass(Callback.class);

    @Before
    void before1() {
        callback.before1();
    }

    @Before
    void before2() {
        callback.before2();
    }

    @Step("step1")
    void step1() {
        callback.step1();
    }

    @Step("step2")
    void step2() {
        callback.step2();
    }

    @Step("Step with parameter: {parameterName}")
    void parameterizedStep(String parameterName) {
        callback.parameterizedStep(parameterName);
    }

    @Step("Step with parameter passed by explicit name bindings: {bound}")
    void parameterizedStepWithBinding(@Binding("bound") String param) {
        callback.parameterizedStepWithBinding(param);
    }

    @Step("Step with mismatching parameter: {mismatch}")
    void stepWithMissingParameter(String unused) {
        callback.stepWithMissingParameter(unused);
    }

    @After
    void after1() {
        callback.after1();
    }

    @After
    void after2() {
        callback.after2();
    }

    public static void reset() {
        clearJMockk(callback);
    }

    public interface Callback {

        void before1();

        void before2();

        void step1();

        void step2();

        void parameterizedStep(String parameterName);

        void parameterizedStepWithBinding(String param);

        void stepWithMissingParameter(@NotNull String unused);

        void after1();

        void after2();
    }

}
