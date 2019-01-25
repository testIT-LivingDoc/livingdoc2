package org.livingdoc.junit.engine

import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine
import org.livingdoc.junit.engine.descriptors.EngineDescriptor
import org.livingdoc.junit.engine.descriptors.ExecutableDocumentDescriptor
import org.livingdoc.junit.engine.discovery.ClassSelectorHandler
import org.livingdoc.junit.engine.discovery.ClasspathRootSelectorHandler
import org.livingdoc.junit.engine.discovery.PackageSelectorHandler
import java.util.*

class LivingDocTestEngine : HierarchicalTestEngine<LivingDocContext>() {

    // TODO override getVersion()

    private val classpathRootSelectorHandler = ClasspathRootSelectorHandler()
    private val packageSelectorHandler = PackageSelectorHandler()
    private val classSelectorHandler = ClassSelectorHandler()

    override fun getId(): String = "livingdoc"
    override fun getGroupId(): Optional<String> = Optional.of("org.livingdoc")
    override fun getArtifactId(): Optional<String> = Optional.of("livingdoc-junit-engine")

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val documentClasses = linkedSetOf<Class<*>>()
        resolveClasspathRootSelectors(discoveryRequest)
                .forEach { documentClasses.add(it) }
        resolvePackageSelectors(discoveryRequest)
                .forEach { documentClasses.add(it) }
        resolveClassSelectors(discoveryRequest)
                .forEach { documentClasses.add(it) }
        return buildEngineDescriptor(uniqueId, documentClasses)
    }

    private fun resolveClasspathRootSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
                .getSelectorsByType(ClasspathRootSelector::class.java)
                .map(classpathRootSelectorHandler::selectDocumentClasses)
                .flatten()
    }

    private fun resolvePackageSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
                .getSelectorsByType(PackageSelector::class.java)
                .map(packageSelectorHandler::selectDocumentClasses)
                .flatten()
    }

    private fun resolveClassSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
                .getSelectorsByType(ClassSelector::class.java)
                .map(classSelectorHandler::selectDocumentClasses)
                .flatten()
    }

    private fun buildEngineDescriptor(engineId: UniqueId, documentClasses: LinkedHashSet<Class<*>>): EngineDescriptor {
        val engineDescriptor = EngineDescriptor(engineId)
        documentClasses
                .map { toDocumentDescriptor(engineId, it) }
                .forEach { engineDescriptor.addChild(it) }
        return engineDescriptor
    }

    private fun toDocumentDescriptor(uniqueEngineId: UniqueId, documentClass: Class<*>): ExecutableDocumentDescriptor {
        val uniqueDocumentId = uniqueId(uniqueEngineId, documentClass)
        return ExecutableDocumentDescriptor(uniqueDocumentId, documentClass)
    }

    private fun uniqueId(uniqueId: UniqueId, documentClass: Class<*>): UniqueId {
        return uniqueId.append("documentClass", documentClass.name)
    }

    override fun createExecutionContext(request: ExecutionRequest): LivingDocContext {
        return LivingDocContext()
    }
}
