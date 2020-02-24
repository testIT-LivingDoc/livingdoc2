package org.livingdoc.jvm.scenario

import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.scenario.matching.ScenarioStepMatcher
import org.livingdoc.scenario.matching.StepTemplate
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions

class ScenarioFixtureModel(
    val fixtureClass: KClass<*>
) {
    // val x: List<KFunction<T>>
    val stepMethods: List<KFunction>
    val stepTemplateToMethod: Map<StepTemplate, KFunction>

    private val stepMatcher: ScenarioStepMatcher

    init {

        // method analysis
        val stepMethods = mutableListOf<KFunction>()

        // TODO something here

        fixtureClass.declaredFunctions.forEach {
            method ->
            if (method.annotations.equals(Step::class)) stepMethods.add(method)
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

    fun getMatchingStepTemplate(step: String): ScenarioStepMatcher.MatchingResult = stepMatcher.match(step)
    fun getStepMethod(template: StepTemplate): Method = stepTemplateToMethod[template]!!

    private fun Method.isAnnotatedWith(annotationClass: KClass<out Annotation>): Boolean {
        return this.isAnnotationPresent(annotationClass.java)
    }
}
