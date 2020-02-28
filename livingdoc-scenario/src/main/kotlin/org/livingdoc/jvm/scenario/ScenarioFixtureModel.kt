package org.livingdoc.jvm.scenario

import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.scenario.matching.StepTemplate
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class ScenarioFixtureModel(val context: FixtureContext) {

    val stepFunctions: List<KFunction<*>> = context.fixtureClass.declaredMemberFunctions
        .filter { it.hasAnnotation<Step>() }

    val stepTemplateToMethod: Map<StepTemplate, KFunction<*>>

    val stepTemplates: List<StepTemplate>

    init {

        val stepTemplateToMethod = mutableMapOf<StepTemplate, KFunction<*>>()
        stepFunctions.map { member ->
            member.findAnnotation<Step>()!!.value.asIterable().map { StepTemplate.parse(it) }.forEach {
                stepTemplateToMethod[it] = member
            }
        }
        this.stepTemplateToMethod = stepTemplateToMethod
        this.stepTemplates = stepTemplateToMethod.keys.toList()
    }
}
