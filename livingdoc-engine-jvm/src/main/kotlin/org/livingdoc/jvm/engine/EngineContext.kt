package org.livingdoc.jvm.engine

import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.engine.extension.ContextImpl

internal class EngineContext(
    parent: EngineContext?,
    val extensionContext: ExtensionContext

) : ContextImpl<EngineContext>(parent) {
    val throwableCollector: ThrowableCollector = ThrowableCollector()
    lateinit var extensions: List<Extension>
}
