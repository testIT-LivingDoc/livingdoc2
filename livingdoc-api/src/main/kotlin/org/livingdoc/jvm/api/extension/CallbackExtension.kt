package org.livingdoc.jvm.api.extension

import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext

/**
 * The Extensions interface used to extend the lifecycle of the Livingdoc tests. The Extension can listen to different
 * lifecycle events of a test execution in livingdoc. For each lifecycle hook the context of the execution is passed to
 * the Extension. The Context contains reference to the internal representation of the test case and the a reference to
 * the class refection object.
 */
interface CallbackExtension : Extension {

    fun onBeforeGroup(context: GroupContext) {
        // optional
    }

    fun onBeforeDocument(context: DocumentFixtureContext) {
        // optional
    }

    fun onBeforeFixture(context: FixtureContext) {
        // optional
    }

    fun onAfterFixture(context: FixtureContext) {
        // optional
    }

    fun onAfterDocument(context: DocumentFixtureContext) {
        // optional
    }

    fun onAfterGroup(context: GroupContext) {
        // optional
    }
}
