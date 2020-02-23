package org.livingdoc.jvm.engine

import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.engine.extension.context.ContextImpl

internal class EngineContext(
    parent: EngineContext?,
    val extensionContext: ExtensionContext,
    val extensions: List<Extension>
) : ContextImpl<EngineContext>(parent) {
    val throwableCollector: ThrowableCollector = ThrowableCollector()
}
