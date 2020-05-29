package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.engine.execution.ScopedFixtureModel
import org.livingdoc.engine.execution.examples.scenarios.matching.ScenarioStepMatcher
import org.livingdoc.engine.execution.examples.scenarios.matching.ScenarioStepMatcher.MatchingResult
import org.livingdoc.engine.execution.examples.scenarios.matching.StepTemplate
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal class ScenarioFixtureModel(
    val fixtureClass: Class<*>
) : ScopedFixtureModel(fixtureClass) {
    val stepMethods: List<Method>
    val stepTemplateToMethod: Map<StepTemplate, Method>

    private val stepMatcher: ScenarioStepMatcher

    init {

        // method analysis
        val stepMethods = mutableListOf<Method>()

        fixtureClass.declaredMethods.forEach { method ->
            if (method.isAnnotatedWith(Step::class)) stepMethods.add(method)
            if (method.isAnnotatedWith(Step.Steps::class)) stepMethods.add(method)
        }

        this.stepMethods = stepMethods

        // step alias analysis

        val stepAliases = mutableSetOf<String>()

        val stepTemplateToMethod = mutableMapOf<StepTemplate, Method>()
        stepMethods.forEach { method ->
            method.getAnnotationsByType(Step::class.java)
                .flatMap { it.value.asIterable() }
                .forEach { alias ->
                    stepAliases.add(alias)
                    stepTemplateToMethod[StepTemplate.parse(alias)] = method
                }
        }

        this.stepTemplateToMethod = stepTemplateToMethod
        this.stepMatcher = ScenarioStepMatcher(stepTemplateToMethod.keys.toList())
    }

    fun getMatchingStepTemplate(step: String): MatchingResult = stepMatcher.match(step)
    fun getStepMethod(template: StepTemplate): Method = stepTemplateToMethod[template]!!

    private fun Method.isAnnotatedWith(annotationClass: KClass<out Annotation>): Boolean {
        return this.isAnnotationPresent(annotationClass.java)
    }
}
