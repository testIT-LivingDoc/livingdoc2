package org.livingdoc.engine

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureWrapper
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureWrapper
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal class DocumentFixtureModel(
    private val documentClass: Class<*>
) {
    val beforeMethods: List<Method>
    val afterMethods: List<Method>

    val decisionTableFixtures: List<DecisionTableFixtureWrapper>
    val scenarioFixtures: List<ScenarioFixtureWrapper>

    init {
        val beforeMethods = mutableListOf<Method>()
        val afterMethods = mutableListOf<Method>()

        documentClass.declaredMethods.forEach { method ->
            if (method.isAnnotationPresent(Before::class.java)) beforeMethods.add(method)
            if (method.isAnnotationPresent(After::class.java)) afterMethods.add(method)
        }

        this.beforeMethods = beforeMethods.sortedBy { it.name }
        this.afterMethods = afterMethods.sortedBy { it.name }

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
