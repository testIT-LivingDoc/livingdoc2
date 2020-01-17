package org.livingdoc.engine.execution.examples.decisiontables;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.fixtures.decisiontables.*;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;

@DecisionTableFixture(parallel = true)
public class LifeCycleFixtureParallel {

    public static Callback callback = mockkJClass(Callback.class);

    @Before
    static void beforeTable() {
        callback.beforeTable();
    }

    @BeforeRow
    void beforeRow() {
        callback.beforeRow();
    }

    @Input("input")
    void input(String value) {
        callback.input(value);
    }

    @BeforeFirstCheck
    void beforeFirstCheck() {
        callback.beforeFirstCheck();
    }

    @Check("check")
    void check(String value) {
        callback.check(value);
    }

    @AfterRow
    void afterRow() {
        callback.afterRow();
    }

    @After
    static void afterTable() {
        callback.afterTable();
    }

    public static void reset() {
        clearJMockk(callback);
    }

    public interface Callback {

        void beforeTable();

        void beforeRow();

        void input(String value);

        void beforeFirstCheck();

        void check(String value);

        void afterRow();

        void afterTable();

    }

}
