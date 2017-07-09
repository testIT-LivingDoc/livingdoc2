package org.livingdoc.engine.execution.examples.decisiontables;

import static org.mockito.Mockito.mock;

import org.livingdoc.fixture.api.decisiontable.AfterRow;
import org.livingdoc.fixture.api.decisiontable.AfterTable;
import org.livingdoc.fixture.api.decisiontable.BeforeFirstCheck;
import org.livingdoc.fixture.api.decisiontable.BeforeRow;
import org.livingdoc.fixture.api.decisiontable.BeforeTable;
import org.livingdoc.fixture.api.decisiontable.Check;
import org.livingdoc.fixture.api.decisiontable.Input;
import org.mockito.Mockito;


public class LifeCycleFixture {

    public static Callback callback = mock(Callback.class);

    @BeforeTable
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

    @AfterTable
    static void afterTable() {
        callback.afterTable();
    }

    public static void reset() {
        Mockito.reset(callback);
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
