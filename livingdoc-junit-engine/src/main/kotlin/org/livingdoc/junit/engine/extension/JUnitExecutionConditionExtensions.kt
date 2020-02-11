package org.livingdoc.junit.engine.extension

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extension
import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.Store
import org.livingdoc.jvm.extension.spi.ConditionEvaluationResult
import org.livingdoc.jvm.extension.spi.ExecutionCondition
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class JUnitExecutionConditionExtensions : ExecutionCondition {

    private val spiExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()
        .filterIsInstance<org.junit.jupiter.api.extension.ExecutionCondition>()

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        loadExtensions(context)

        val junitContext = JUnitExtensionContext(context)
        val results = (spiExtensions + context.extensions).map { it.evaluateExecutionCondition(junitContext) }
        val enabled = results.all { !it.isDisabled }
        val reason =
            results.mapNotNull { conditionEvaluationResult -> conditionEvaluationResult.reason.orElse(null) }
                .filter { it.isNotBlank() }.joinToString()

        return ConditionEvaluationResult(enabled, reason)
    }

    private fun loadExtensions(context: ExtensionContext) {
        val extensionTypes = context.testClass.annotations.filterIsInstance<ExtendWith>().flatMap { it.value.toList() }
        val extensions = extensionTypes.filterIsInstance<KClass<org.junit.jupiter.api.extension.ExecutionCondition>>()
            .map { it.createInstance() }
        context.extensions = extensions
    }
}

private val ExtensionContext.store: Store
    get() = getStore("org.livingdoc.junit.engine.extension.JUnitExecutionConditionExtensions")

private var ExtensionContext.extensions: List<org.junit.jupiter.api.extension.ExecutionCondition>
    get() = store.getListCombineAncestors("extensions")
        .filterIsInstance<org.junit.jupiter.api.extension.ExecutionCondition>()
    set(value) {
        store.put("extensions", value)
    }
