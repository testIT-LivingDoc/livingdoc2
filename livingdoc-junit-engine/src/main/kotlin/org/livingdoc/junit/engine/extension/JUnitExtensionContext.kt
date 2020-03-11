package org.livingdoc.junit.engine.extension

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstances
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*

@Suppress("TooManyFunctions")
class JUnitExtensionContext(private val context: org.livingdoc.jvm.api.extension.context.ExtensionContext) :
    ExtensionContext {
    override fun getElement(): Optional<AnnotatedElement> {
        return Optional.of(context.testClass.java)
    }

    override fun getParent(): Optional<ExtensionContext> {
        return Optional.ofNullable(context.parent).map { JUnitExtensionContext(it) }
    }

    override fun getTestInstance(): Optional<Any> {
        return Optional.empty()
    }

    override fun getTestClass(): Optional<Class<*>> {
        return Optional.of(context.testClass.java)
    }

    override fun getTestInstances(): Optional<TestInstances> {
        return Optional.empty()
    }

    override fun getDisplayName(): String {
        return context.testClass.simpleName.orEmpty()
    }

    override fun getUniqueId(): String {
        return context.testClass.qualifiedName ?: UUID.randomUUID().toString()
    }

    override fun getRoot(): ExtensionContext {
        return parent.orElse(null)?.root ?: this
    }

    override fun getExecutionException(): Optional<Throwable> {
        return Optional.empty()
    }

    override fun getTestMethod(): Optional<Method> {
        return Optional.empty()
    }

    override fun getConfigurationParameter(key: String?): Optional<String> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTestInstanceLifecycle(): Optional<TestInstance.Lifecycle> {
        return Optional.empty()
    }

    override fun getTags(): MutableSet<String> {
        return mutableSetOf()
    }

    override fun publishReportEntry(map: MutableMap<String, String>?) {
        throw NotImplementedError()
    }

    override fun getStore(namespace: ExtensionContext.Namespace): ExtensionContext.Store {
        return JUnitStore(context.getStore(namespace.hashCode().toString()))
    }
}
