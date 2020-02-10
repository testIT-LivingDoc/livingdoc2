package org.livingdoc.junit.engine

import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.junit.engine.descriptors.EngineDescriptor
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

        val groupClasses = linkedSetOf<Class<*>>()
        resolveGroupClasspathRootSelectors(discoveryRequest)
            .forEach { groupClasses.add(it) }
        resolveGroupPackageSelectors(discoveryRequest)
            .forEach { groupClasses.add(it) }
        resolveGroupClassSelectors(discoveryRequest)
            .forEach { groupClasses.add(it) }

        val groupsToExecute = filterGroupsToExecute(documentClasses, groupClasses)
        if (groupsToExecute.isNotEmpty()) {
            documentClasses.addAll(detectGroupMembers(groupsToExecute))
        }

        return buildEngineDescriptor(uniqueId, documentClasses)
    }

    /* -_-_-_-_-_ Select executable documents _-_-_-_-_- */

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

    /* -_-_-_-_-_ Select groups _-_-_-_-_- */

    private fun resolveGroupClasspathRootSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
            .getSelectorsByType(ClasspathRootSelector::class.java)
            .map(classpathRootSelectorHandler::selectGroupClasses)
            .flatten()
    }

    private fun resolveGroupPackageSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
            .getSelectorsByType(PackageSelector::class.java)
            .map(packageSelectorHandler::selectGroupClasses)
            .flatten()
    }

    private fun resolveGroupClassSelectors(discoveryRequest: EngineDiscoveryRequest): List<Class<*>> {
        return discoveryRequest
            .getSelectorsByType(ClassSelector::class.java)
            .map(classSelectorHandler::selectGroupClasses)
            .flatten()
    }

    /* -_-_-_-_-_-_-_-_-_-_- */

    private fun buildEngineDescriptor(engineId: UniqueId, documentClasses: LinkedHashSet<Class<*>>): EngineDescriptor {
        return EngineDescriptor(engineId, documentClasses.map { it.kotlin })
    }

    override fun createExecutionContext(request: ExecutionRequest): LivingDocContext {
        return LivingDocContext()
    }

    /**
     * Filters the group in a set and returns only those that should be executed
     */
    private fun filterGroupsToExecute(documentClasses: Set<Class<*>>, groupClasses: Set<Class<*>>): Set<Class<*>> {
        return groupClasses.filter { group ->
            group.declaredClasses.none { innerClass ->
                documentClasses.contains(innerClass)
            }
        }.toSet()
    }

    /**
     * Finds and returns all members of the given groups
     * WARNING: This iterates over all classes in the current classloader
     */
    private fun detectGroupMembers(groupClasses: Set<Class<*>>): Set<Class<*>> {
        val classFilter = { candidateClass: Class<*> ->
            candidateClass.isAnnotationPresent(ExecutableDocument::class.java)
        }
        val everyNameIsOk = { _: String -> true }

        // Find all group members
        val allClasses = Package.getPackages().flatMap { pkg ->
            ReflectionUtils.findAllClassesInPackage(pkg.name, classFilter, everyNameIsOk)
        }
        val groupMembers = allClasses.groupBy { document ->
            document.getAnnotation(ExecutableDocument::class.java).group.java
                .takeIf { groupCandidate -> groupCandidate.isAnnotationPresent(Group::class.java) }
                ?: document.enclosingClass?.takeIf { groupCandidate ->
                    groupCandidate.isAnnotationPresent(Group::class.java)
                }
        }

        return groupClasses.flatMap { group -> groupMembers[group].orEmpty() }.toSet()
    }
}
