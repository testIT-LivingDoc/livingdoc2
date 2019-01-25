package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.api.fixtures.decisiontables.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

class DecisionTableFixtureModel(
    val fixtureClass: Class<*>
) {

    val beforeTableMethods: List<Method>
    val afterTableMethods: List<Method>
    val beforeRowMethods: List<Method>
    val afterRowMethods: List<Method>
    val beforeFirstCheckMethods: List<Method>

    val inputFields: List<Field>
    val inputMethods: List<Method>
    private val inputAliases: MutableSet<String>
    private val inputAliasToField: MutableMap<String, Field>
    private val inputAliasToMethod: MutableMap<String, Method>

    val checkMethods: List<Method>
    private val checkAliases: Set<String>
    private val checkAliasToMethod: Map<String, Method>

    val aliases: Set<String>
        get() = inputAliases + checkAliases

    init {

        // method analysis

        val beforeTableMethods = mutableListOf<Method>()
        val afterTableMethods = mutableListOf<Method>()
        val beforeRowMethods = mutableListOf<Method>()
        val afterRowMethods = mutableListOf<Method>()
        val beforeFirstCheckMethods = mutableListOf<Method>()
        val inputMethods = mutableListOf<Method>()
        val checkMethods = mutableListOf<Method>()

        fixtureClass.declaredMethods.forEach { method ->
            if (method.isAnnotatedWith(BeforeTable::class)) beforeTableMethods.add(method)
            if (method.isAnnotatedWith(AfterTable::class)) afterTableMethods.add(method)
            if (method.isAnnotatedWith(BeforeRow::class)) beforeRowMethods.add(method)
            if (method.isAnnotatedWith(AfterRow::class)) afterRowMethods.add(method)
            if (method.isAnnotatedWith(BeforeFirstCheck::class)) beforeFirstCheckMethods.add(method)
            if (method.isAnnotatedWith(Input::class)) inputMethods.add(method)
            if (method.isAnnotatedWith(Check::class)) checkMethods.add(method)
        }

        this.beforeTableMethods = beforeTableMethods
        this.afterTableMethods = afterTableMethods
        this.beforeRowMethods = beforeRowMethods
        this.afterRowMethods = afterRowMethods
        this.beforeFirstCheckMethods = beforeFirstCheckMethods
        this.inputMethods = inputMethods
        this.checkMethods = checkMethods

        // field analysis

        val inputFields = mutableListOf<Field>()

        fixtureClass.declaredFields.forEach { field ->
            if (field.isAnnotatedWith(Input::class)) inputFields.add(field)
        }

        this.inputFields = inputFields

        // input alias analysis

        val inputAliases = mutableSetOf<String>()

        val inputAliasToField = mutableMapOf<String, Field>()
        inputFields.forEach { field ->
            field.getAnnotationsByType(Input::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
                        inputAliases.add(alias)
                        inputAliasToField[alias] = field
                    }
        }

        val inputAliasToMethod = mutableMapOf<String, Method>()
        inputMethods.forEach { method ->
            method.getAnnotationsByType(Input::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
                        inputAliases.add(alias)
                        inputAliasToMethod[alias] = method
                    }
        }

        this.inputAliases = inputAliases
        this.inputAliasToField = inputAliasToField
        this.inputAliasToMethod = inputAliasToMethod

        // check alias analysis

        val checkAliases = mutableSetOf<String>()

        val checkAliasToMethod = mutableMapOf<String, Method>()
        checkMethods.forEach { method ->
            method.getAnnotationsByType(Check::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
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

    fun getInputField(alias: String): Field? = inputAliasToField[alias]
    fun getInputMethod(alias: String): Method? = inputAliasToMethod[alias]

    fun isCheckAlias(alias: String): Boolean = checkAliases.contains(alias)
    fun getCheckMethod(alias: String): Method? = checkAliasToMethod[alias]

    private fun Method.isAnnotatedWith(annotationClass: KClass<out Annotation>): Boolean {
        return this.isAnnotationPresent(annotationClass.java)
    }

    private fun Field.isAnnotatedWith(annotationClass: KClass<out Annotation>): Boolean {
        return this.isAnnotationPresent(annotationClass.java)
    }
}
