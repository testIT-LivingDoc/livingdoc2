package org.livingdoc.jvm.engine

import org.livingdoc.config.ConfigProvider
import org.livingdoc.results.documents.DocumentResult
import kotlin.reflect.KClass

class LivingDoc(val configProvider: ConfigProvider = ConfigProvider.load()) {
    fun execute(documentClasses: List<KClass<*>>): List<DocumentResult> {
        return emptyList()
    }
}
