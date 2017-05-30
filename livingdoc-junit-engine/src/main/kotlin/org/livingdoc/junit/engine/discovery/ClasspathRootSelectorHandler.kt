package org.livingdoc.junit.engine.discovery

import org.junit.platform.commons.util.ReflectionUtils.findAllClassesInClasspathRoot
import org.junit.platform.engine.discovery.ClasspathRootSelector

class ClasspathRootSelectorHandler : SelectorHandler<ClasspathRootSelector>() {

    private val everyNameIsOk = { _: String -> true }

    override fun selectDocumentClasses(selector: ClasspathRootSelector, classFilter: (Class<*>) -> Boolean):
            List<Class<*>> {
        return findAllClassesInClasspathRoot(selector.classpathRoot, classFilter, everyNameIsOk)
    }

}
