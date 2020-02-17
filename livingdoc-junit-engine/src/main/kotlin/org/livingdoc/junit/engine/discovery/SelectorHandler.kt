package org.livingdoc.junit.engine.discovery

import org.junit.platform.engine.DiscoverySelector
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group

abstract class SelectorHandler<in T : DiscoverySelector> {

    private val executableDocumentAnnotationIsPresent = { candidateClass: Class<*> ->
        candidateClass.isAnnotationPresent(ExecutableDocument::class.java)
    }

    private val groupAnnotationIsPresent = { candidateClass: Class<*> ->
        candidateClass.isAnnotationPresent(Group::class.java)
    }

    /**
     * Select all Document classes
     */
    fun selectDocumentClasses(selector: T): List<Class<*>> {
        return selectDocumentClasses(selector, executableDocumentAnnotationIsPresent)
    }

    /**
     * Select all Group classes
     */
    fun selectGroupClasses(selector: T): List<Class<*>> {
        return selectDocumentClasses(selector, groupAnnotationIsPresent)
    }

    protected abstract fun selectDocumentClasses(selector: T, classFilter: (Class<*>) -> Boolean): List<Class<*>>
}
