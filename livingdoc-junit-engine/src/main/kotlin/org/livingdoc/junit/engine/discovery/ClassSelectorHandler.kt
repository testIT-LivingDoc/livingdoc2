package org.livingdoc.junit.engine.discovery

import org.junit.platform.engine.discovery.ClassSelector

class ClassSelectorHandler : SelectorHandler<ClassSelector>() {

    override fun selectDocumentClasses(selector: ClassSelector, classFilter: (Class<*>) -> Boolean): List<Class<*>> {
        val javaClass = selector.javaClass
        if (classFilter.invoke(javaClass)) {
            return listOf(javaClass)
        }
        // TODO: not qualified == error?
        return listOf()
    }
}
