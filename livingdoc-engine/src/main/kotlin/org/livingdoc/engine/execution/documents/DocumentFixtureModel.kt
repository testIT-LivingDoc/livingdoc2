package org.livingdoc.engine.execution.documents

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.FailFast
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.tagging.Tag
import org.livingdoc.engine.execution.ScopedFixtureModel
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureWrapper
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureWrapper
import kotlin.reflect.KClass

/**
 * A DocumentFixtureModel is a representation of the glue code necessary to execute a [DocumentFixture]
 *
 * @see DocumentFixture
 * @see DocumentExecution
 */
internal class DocumentFixtureModel(
    private val documentClass: Class<*>
) : ScopedFixtureModel(documentClass) {

    val decisionTableFixtures: List<DecisionTableFixtureWrapper>
    val scenarioFixtures: List<ScenarioFixtureWrapper>
    /**
     * getFailFastExceptions returns the specified exceptions for fail fast if the annotation is set.
     * Otherwise it returns an empty list.
     *
     * @return a list of Exception classes that should be considered for fail fast
     */
    val failFastExceptions: List<Class<*>>
        get() {
            return documentClass.getAnnotation(FailFast::class.java)?.onExceptionTypes?.map { it.java } ?: emptyList()
        }

    /**
     * getTags returns the specified tags of a Document or an empty list
     *
     * @return a list of Strings that represents the tags
     */
    val tags: List<String>
        get() {
            return documentClass.getAnnotationsByType(Tag::class.java).orEmpty().map { it.value }
        }

    init {
        decisionTableFixtures = getFixtures(DecisionTableFixture::class).map {
            DecisionTableFixtureWrapper(it)
        }

        scenarioFixtures = getFixtures(ScenarioFixture::class).map {
            ScenarioFixtureWrapper(it)
        }
    }

    /**
     * GetFixtures loads all candidate classes with a given annotation
     *
     * @param annotationClass the annotation class to filter for
     * @return a list of fixture classes that have the specified annotation present
     */
    private fun getFixtures(annotationClass: KClass<out Annotation>): List<Class<*>> {
        val declaredInside = documentClass.declaredClasses
            .filter { it.isAnnotationPresent(annotationClass.java) }
        val fromAnnotation = documentClass.getAnnotation(ExecutableDocument::class.java)!!.fixtureClasses
            .map { it.java }
            .filter { it.isAnnotationPresent(annotationClass.java) }
        return declaredInside + fromAnnotation
    }
}
