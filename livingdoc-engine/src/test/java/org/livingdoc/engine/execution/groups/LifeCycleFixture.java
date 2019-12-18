package org.livingdoc.engine.execution.groups;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.documents.Group;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;

@Group
public class LifeCycleFixture {
    public static Callback callback = mockkJClass(Callback.class);

    @Before
    public static void before() {
        callback.before();
    }

    @After
    public static void after() {
        callback.after();
    }

    public static void reset() {
        clearJMockk(callback);
    }

    interface Callback {
        void before();
        void after();
    }
}
