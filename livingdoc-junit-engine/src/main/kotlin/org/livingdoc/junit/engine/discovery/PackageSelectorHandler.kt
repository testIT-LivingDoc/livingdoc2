package org.livingdoc.junit.engine.discovery

import org.junit.platform.commons.util.ReflectionUtils.findAllClassesInPackage
import org.junit.platform.engine.discovery.PackageSelector

class PackageSelectorHandler : SelectorHandler<PackageSelector>() {

    private val everyNameIsOk = { _: String -> true }

    override fun selectDocumentClasses(selector: PackageSelector, classFilter: (Class<*>) -> Boolean): List<Class<*>> {
        return findAllClassesInPackage(selector.packageName, classFilter, everyNameIsOk)
    }

}
