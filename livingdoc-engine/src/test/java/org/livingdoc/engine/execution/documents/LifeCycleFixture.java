package org.livingdoc.engine.execution.documents;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;
import org.livingdoc.api.documents.ExecutableDocument;
import org.livingdoc.api.tagging.Tag;

import static org.livingdoc.engine.MockkExtKt.clearJMockk;
import static org.livingdoc.engine.MockkExtKt.mockkJClass;

@Tag("test")
@Tag("stub")
@ExecutableDocument("")
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
