package org.livingdoc.jvm.engine

import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.engine.extension.context.ContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionRegistry

internal class EngineContext(
    parent: EngineContext?,
    val extensionContext: ExtensionContext,
    val extensionRegistry: ExtensionRegistry
) : ContextImpl<EngineContext>(parent) {
    val throwableCollector: ThrowableCollector = ThrowableCollector()
}
