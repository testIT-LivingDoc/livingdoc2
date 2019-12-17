package org.livingdoc.engine.execution.documents

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.engine.execution.ScopedFixtureModel
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureWrapper
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureWrapper
import kotlin.reflect.KClass

internal class DocumentFixtureModel(
    private val documentClass: Class<*>
) : ScopedFixtureModel(documentClass) {

    val decisionTableFixtures: List<DecisionTableFixtureWrapper>
    val scenarioFixtures: List<ScenarioFixtureWrapper>

    init {
        decisionTableFixtures = getFixtures(documentClass, DecisionTableFixture::class).map {
            DecisionTableFixtureWrapper(it)
        }

        scenarioFixtures = getFixtures(documentClass, ScenarioFixture::class).map {
            ScenarioFixtureWrapper(it)
        }
    }

    private fun getFixtures(document: Class<*>, annotationClass: KClass<out Annotation>): List<Class<*>> {
        val declaredInside = document.declaredClasses
                .filter { it.isAnnotationPresent(annotationClass.java) }
        val fromAnnotation = document.getAnnotation(ExecutableDocument::class.java)!!.fixtureClasses
                .map { it.java }
                .filter { it.isAnnotationPresent(annotationClass.java) }
        return declaredInside + fromAnnotation
    }
}
