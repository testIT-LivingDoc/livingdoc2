package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.AfterRow
import org.livingdoc.api.fixtures.decisiontables.BeforeFirstCheck
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.repositories.model.decisiontable.Field
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberFunctions
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
    val beforeRowMethods: List<KCallable<*>>
    val afterRowMethods: List<KCallable<*>>
    val beforeFirstCheckMethods: List<KCallable<*>>

    val inputFields: List<KProperty<*>>
    val inputMethods: List<KCallable<*>>
    private val inputAliases: MutableSet<String>
    private val inputAliasToField: MutableMap<String, KProperty<*>>
    private val inputAliasToMethod: MutableMap<String, KCallable<*>>

    val checkMethods: List<KCallable<*>>
    private val checkAliases: Set<String>
    private val checkAliasToMethod: Map<String, KCallable<*>>

    val aliases: Set<String>
        get() = inputAliases + checkAliases

    val beforeMethods: List<KCallable<*>> = fixtureClass.declaredMembers.filter { method ->
        method.hasAnnotation<Before>()
    }.sortedBy { method -> method.name }

    val afterMethods: List<KCallable<*>> = fixtureClass.declaredMembers.filter { method ->
        method.hasAnnotation<After>()
    }.sortedBy { method -> method.name }

    init {
        // method analysis

        val beforeRowMethods = mutableListOf<KCallable<*>>()
        val afterRowMethods = mutableListOf<KCallable<*>>()
        val beforeFirstCheckMethods = mutableListOf<KCallable<*>>()
        val inputMethods = mutableListOf<KCallable<*>>()
        val checkMethods = mutableListOf<KCallable<*>>()

        fixtureClass.declaredMemberFunctions.forEach { method ->
            if (method.hasAnnotation<BeforeRow>()) beforeRowMethods.add(method)
            if (method.hasAnnotation<AfterRow>()) afterRowMethods.add(method)
            if (method.hasAnnotation<BeforeFirstCheck>()) beforeFirstCheckMethods.add(method)
            if (method.hasAnnotation<Input>()) inputMethods.add(method)
            if (method.hasAnnotation<Check>()) checkMethods.add(method)
        }

        this.beforeRowMethods = beforeRowMethods
        this.afterRowMethods = afterRowMethods
        this.beforeFirstCheckMethods = beforeFirstCheckMethods
        this.inputMethods = inputMethods
        this.checkMethods = checkMethods

        // field analysis

        val inputFields = mutableListOf<KProperty<*>>()
        fixtureClass.declaredMembers.forEach { field ->
            if (field is KProperty && field.hasAnnotation<Input>()) inputFields.add(field)
        }
        this.inputFields = inputFields

        // input alias analysis

        val inputAliases = mutableSetOf<String>()
        val inputAliasToField = mutableMapOf<String, KProperty<*>>()
        inputFields.forEach { field ->
            val alias = field.findAnnotation<Input>()?.value
            if (alias != null) {
                inputAliases.add(alias)
                inputAliasToField[alias] = field
            }
        }

        val inputAliasToMethod = mutableMapOf<String, KCallable<*>>()
        inputMethods.forEach { method ->
            val alias = method.findAnnotation<Input>()?.value
            if (alias != null) {
                inputAliases.add(alias)
                inputAliasToMethod[alias] = method
            }
        }
        this.inputAliases = inputAliases
        this.inputAliasToField = inputAliasToField
        this.inputAliasToMethod = inputAliasToMethod

        // check alias analysis

        val checkAliases = mutableSetOf<String>()
        val checkAliasToMethod = mutableMapOf<String, KCallable<*>>()
        checkMethods.forEach { method ->
            val alias = method.findAnnotation<Check>()?.value
            if (alias != null) {
                checkAliases.add(alias)
                checkAliasToMethod[alias] = method
            }
        }
        this.checkAliases = checkAliases
        this.checkAliasToMethod = checkAliasToMethod
    }

    fun isInputAlias(alias: String): Boolean = inputAliases.contains(alias)
    fun isFieldInput(alias: String): Boolean = inputAliasToField.containsKey(alias)
    fun isMethodInput(alias: String): Boolean = inputAliasToMethod.containsKey(alias)

    fun getInputField(alias: String): KProperty<*>? = inputAliasToField[alias]
    fun getInputMethod(alias: String): KCallable<*>? = inputAliasToMethod[alias]

    fun isCheckAlias(alias: String): Boolean = checkAliases.contains(alias)
    fun getCheckMethod(alias: String): KCallable<*>? = checkAliasToMethod[alias]
}
