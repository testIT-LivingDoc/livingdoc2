package org.livingdoc.junit.engine.discovery

import org.junit.platform.engine.DiscoverySelector
import org.livingdoc.api.documents.ExecutableDocument

abstract class SelectorHandler<in T : DiscoverySelector> {

    private val executableDocumentAnnotationIsPresent = { candidateClass: Class<*> ->
        candidateClass.isAnnotationPresent(ExecutableDocument::class.java)
    }

    fun selectDocumentClasses(selector: T): List<Class<*>> {
        return selectDocumentClasses(selector, executableDocumentAnnotationIsPresent)
    }

    protected abstract fun selectDocumentClasses(selector: T, classFilter: (Class<*>) -> Boolean): List<Class<*>>

}
