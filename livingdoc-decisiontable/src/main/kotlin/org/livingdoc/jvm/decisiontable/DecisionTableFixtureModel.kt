package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.fixtures.decisiontables.AfterRow
import org.livingdoc.api.fixtures.decisiontables.BeforeFirstCheck
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * This class represents the class of a a Decision Table Fixture
 */
class DecisionTableFixtureModel(
    val fixtureClass: KClass<*>
) {
    /**
     * Can the steps of this fixture be executed in parallel
     */
    val parallelExecution: Boolean = fixtureClass.findAnnotation<DecisionTableFixture>()?.parallel ?: false

    /**
     * Lists of methods
     */
    val beforeRowMethods: List<KFunction<*>> =
        fixtureClass.declaredMemberFunctions.filter { it.hasAnnotation<BeforeRow>() }
    val afterRowMethods: List<KFunction<*>> =
        fixtureClass.declaredMemberFunctions.filter { it.hasAnnotation<AfterRow>() }
    val beforeFirstCheckMethods: List<KFunction<*>> =
        fixtureClass.declaredMemberFunctions.filter { it.hasAnnotation<BeforeFirstCheck>() }

    val beforeMethods: List<KCallable<*>> = fixtureClass.declaredMembers.filter { method ->
        method.hasAnnotation<Before>()
    }.sortedBy { method -> method.name }

    val afterMethods: List<KCallable<*>> = fixtureClass.declaredMembers.filter { method ->
        method.hasAnnotation<After>()
    }.sortedBy { method -> method.name }

    val inputFields: List<KMutableProperty<*>> =
        fixtureClass.declaredMemberProperties.filter { field -> field.hasAnnotation<Input>() }
            .filterIsInstance<KMutableProperty<*>>()
    val inputMethods: List<KFunction<*>> = fixtureClass.declaredMemberFunctions.filter { it.hasAnnotation<Input>() }
    val checkMethods: List<KFunction<*>> = fixtureClass.declaredMemberFunctions.filter { it.hasAnnotation<Check>() }

    val aliases: Set<String>
        get() = inputAliases + checkAliases

    private val inputAliases: Set<String> get() = inputAliasToField.keys + inputAliasToMethod.keys
    private val inputAliasToField: Map<String, KMutableProperty<*>> = inputFields.map { field ->
        val alias = field.findAnnotation<Input>()!!.value
        alias to field
    }.toMap()
    private val inputAliasToMethod: Map<String, KFunction<*>> = inputMethods.map { method ->
        val alias = method.findAnnotation<Input>()!!.value
        alias to method
    }.toMap()

    private val checkAliases: Set<String> get() = checkAliasToMethod.keys
    private val checkAliasToMethod: Map<String, KFunction<*>> = checkMethods.map { method ->
        val alias = method.findAnnotation<Check>()!!.value
        alias to method
    }.toMap()

    fun isInputAlias(alias: String): Boolean = inputAliases.contains(alias)
    fun isFieldInput(alias: String): Boolean = inputAliasToField.containsKey(alias)
    fun isMethodInput(alias: String): Boolean = inputAliasToMethod.containsKey(alias)

    fun getInputField(alias: String): KMutableProperty<*>? = inputAliasToField[alias]
    fun getInputMethod(alias: String): KFunction<*>? = inputAliasToMethod[alias]
    fun getInput(alias: String): KCallable<*>? = getInputField(alias)?.setter ?: getInputMethod(alias)

    fun isCheckAlias(alias: String): Boolean = checkAliases.contains(alias)
    fun getCheckMethod(alias: String): KFunction<*>? = checkAliasToMethod[alias]
}
