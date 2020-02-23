package org.livingdoc.jvm.engine.extension.defaults

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.jvm.api.extension.CallbackExtension
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.engine.callStaticAnnotatedFunctionWithNoArguments

class LifecycleMethodAdapter : CallbackExtension {
    override fun onBeforeGroup(context: GroupContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<Before>()
    }

    override fun onBeforeDocument(context: DocumentFixtureContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<Before>()
    }

    override fun onBeforeFixture(context: FixtureContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<Before>()
    }

    override fun onAfterFixture(context: FixtureContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<After>()
    }

    override fun onAfterDocument(context: DocumentFixtureContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<After>()
    }

    override fun onAfterGroup(context: GroupContext) {
        context.testClass.callStaticAnnotatedFunctionWithNoArguments<After>()
    }
}
