package org.livingdoc.engine.execution.examples.decisiontables;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.fixtures.decisiontables.*;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;

@DecisionTableFixture
public class ExtendedLifeCycleFixture {

    public static Callback callback = mockkJClass(Callback.class);

    @Before
    static void beforeTable1() {
        callback.beforeTable1();
    }

    @Before
    static void beforeTable2() {
        callback.beforeTable2();
    }

    @BeforeRow
    void beforeRow1() {
        callback.beforeRow1();
    }

    @BeforeRow
    void beforeRow2() {
        callback.beforeRow2();
    }

    @Input("input")
    void input(String value) {
        callback.input(value);
    }

    @BeforeFirstCheck
    void beforeFirstCheck1() {
        callback.beforeFirstCheck1();
    }

    @BeforeFirstCheck
    void beforeFirstCheck2() {
        callback.beforeFirstCheck2();
    }

    @Check("check")
    void check(String value) {
        callback.check(value);
    }

    @AfterRow
    void afterRow1() {
        callback.afterRow1();
    }

    @AfterRow
    void afterRow2() {
        callback.afterRow2();
    }

    @After
    static void afterTable1() {
        callback.afterTable1();
    }

    @After
    static void afterTable2() {
        callback.afterTable2();
    }

    public static void reset() {
        clearJMockk(callback);
    }

    public interface Callback {

        void beforeTable1();

        void beforeTable2();

        void beforeRow1();

        void beforeRow2();

        void input(String value);

        void beforeFirstCheck1();

        void beforeFirstCheck2();

        void check(String value);

        void afterRow1();

        void afterRow2();

        void afterTable1();

        void afterTable2();

    }

}
