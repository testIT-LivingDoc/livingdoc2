package org.livingdoc.junit.engine.extension

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extension
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.api.extension.context.Store
import org.livingdoc.jvm.api.extension.CallbackExtension
import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.extension.ExecutionCondition
import java.util.*
import kotlin.reflect.full.createInstance

class JUnitExtensionsAdapter : ExecutionCondition,
    CallbackExtension {

    private val spiExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        loadExtensions(context)

        val junitContext = JUnitExtensionContext(context)
        val results =
            (spiExtensions + context.extensions).filterIsInstance<org.junit.jupiter.api.extension.ExecutionCondition>()
                .map {
                    it.evaluateExecutionCondition(junitContext)
                }
        val enabled = results.all { !it.isDisabled }
        val reason =
            results.mapNotNull { conditionEvaluationResult -> conditionEvaluationResult.reason.orElse(null) }
                .filter { it.isNotBlank() }.joinToString()

        return ConditionEvaluationResult(enabled, reason)
    }

    override fun onBeforeGroup(context: GroupContext) {
        loadExtensions(context)
        onBeforeAllCallback(context)
    }

    override fun onBeforeDocument(context: DocumentFixtureContext) {
        loadExtensions(context)
        onBeforeAllCallback(context)
    }

    override fun onBeforeFixture(context: FixtureContext) {
        loadExtensions(context)
        onBeforeAllCallback(context)
    }

    override fun onAfterFixture(context: FixtureContext) {
        onAfterAllCallback(context)
    }

    override fun onAfterDocument(context: DocumentFixtureContext) {
        onAfterAllCallback(context)
    }

    override fun onAfterGroup(context: GroupContext) {
        onAfterAllCallback(context)
    }

    private fun onBeforeAllCallback(context: ExtensionContext) {
        val junitContext = JUnitExtensionContext(context)
        (spiExtensions + context.extensions).filterIsInstance<BeforeAllCallback>()
            .forEach { it.beforeAll(junitContext) }
    }

    private fun onAfterAllCallback(context: ExtensionContext) {
        val junitContext = JUnitExtensionContext(context)
        (spiExtensions + context.extensions).reversed().filterIsInstance<AfterAllCallback>()
            .forEach { it.afterAll(junitContext) }
    }

    private fun loadExtensions(context: ExtensionContext) {
        if (context.store.get("loadedExtensionsTestClass") == context.testClass) {
            // already loaded
            return
        }
        val extensionTypes = context.testClass.annotations.filterIsInstance<ExtendWith>().flatMap { it.value.toList() }
        val extensions = extensionTypes.map { it.createInstance() }
        context.extensions = extensions
        context.store.put("loadedExtensionsTestClass", context.testClass)
    }
}

private val ExtensionContext.store: Store
    get() = getStore("org.livingdoc.junit.engine.extension.JUnitExtensionsAdapter")

private var ExtensionContext.extensions: List<Extension>
    get() = store.getListCombineAncestors("extensions")
        .filterIsInstance<Extension>()
    set(value) {
        store.put("extensions", value)
    }
