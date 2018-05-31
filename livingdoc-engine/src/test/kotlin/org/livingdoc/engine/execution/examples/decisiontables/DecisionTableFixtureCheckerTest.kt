package org.livingdoc.engine.execution.examples.decisiontables

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass


internal class DecisionTableFixtureCheckerTest {

    val classPrefix = "org.livingdoc.engine.execution.examples.decisiontables.MalformedFixtures"
    val methodPrefix = "void $classPrefix\$"

    @Test fun `fixture must have default constructor`() {
        val errors = executeCheck(MalformedFixtures.NoDefaultConstructor::class)
        assertThat(errors).containsExactly("The fixture class <$classPrefix.NoDefaultConstructor> has no default constructor!")
    }

    @Test fun `before table methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.BeforeTableWithParameter::class)
        assertThat(errors).containsExactly("@BeforeTable method <static ${methodPrefix}BeforeTableWithParameter.beforeTable(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `before table methods must be static`() {
        val errors = executeCheck(MalformedFixtures.NonStaticBeforeTable::class)
        assertThat(errors).containsExactly("@BeforeTable method <${methodPrefix}NonStaticBeforeTable.beforeTable()> must be static!")
    }

    @Test fun `before row methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.BeforeRowWithParameter::class)
        assertThat(errors).containsExactly("@BeforeRow method <${methodPrefix}BeforeRowWithParameter.beforeRow(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `before row methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticBeforeRow::class)
        assertThat(errors).containsExactly("@BeforeRow method <static ${methodPrefix}StaticBeforeRow.beforeRow()> must not be static!")
    }

    @Test fun `two input fields cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.InputFieldsWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <input> is used multiple times!")
    }

    @Test fun `an input field and an input method cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.InputFieldAndMethodWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <input> is used multiple times!")
    }

    @Test fun `two input methods cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.InputMethodsWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <input> is used multiple times!")
    }

    @Test fun `input methods must have exactly one parameter`() {
        val errors = executeCheck(MalformedFixtures.InputMethodWithoutParameter::class)
        assertThat(errors).containsExactly("@Input method <${methodPrefix}InputMethodWithoutParameter.input()> has 0 parameter(s) - must have exactly 1!")
    }

    @Test fun `input methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticInputMethod::class)
        assertThat(errors).containsExactly("@Input method <static ${methodPrefix}StaticInputMethod.input(java.lang.String)> must not be static!")
    }

    @Test fun `input methods and check methods cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.InputAndCheckMethodsWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <name> is used multiple times!")
    }

    @Test fun `input fields and check methods cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.InputFieldAndCheckMethodWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <name> is used multiple times!")
    }

    @Test fun `before first check methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.BeforeFirstCheckWithParameter::class)
        assertThat(errors).containsExactly("@BeforeFirstCheck method <${methodPrefix}BeforeFirstCheckWithParameter.beforeFirstCheck(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `before first check methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticBeforeFirstCheck::class)
        assertThat(errors).containsExactly("@BeforeFirstCheck method <static ${methodPrefix}StaticBeforeFirstCheck.beforeFirstCheck()> must not be static!")
    }

    @Test fun `check methods cant share an alias`() {
        val errors = executeCheck(MalformedFixtures.CheckMethodsWithSameAlias::class)
        assertThat(errors).containsExactly("Alias <check> is used multiple times!")
    }

    @Test fun `check methods must have exactly one parameter`() {
        val errors = executeCheck(MalformedFixtures.CheckMethodWithoutParameter::class)
        assertThat(errors).containsExactly("@Check method <${methodPrefix}CheckMethodWithoutParameter.check()> has 0 parameter(s) - must have exactly 1!")
    }

    @Test fun `check methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticCheckMethod::class)
        assertThat(errors).containsExactly("@Check method <static ${methodPrefix}StaticCheckMethod.check(java.lang.String)> must not be static!")
    }

    @Test fun `after row methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.AfterRowWithParameter::class)
        assertThat(errors).containsExactly("@AfterRow method <${methodPrefix}AfterRowWithParameter.afterRow(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `after row methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticAfterRow::class)
        assertThat(errors).containsExactly("@AfterRow method <static ${methodPrefix}StaticAfterRow.afterRow()> must not be static!")
    }

    @Test fun `after table methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.AfterTableWithParameter::class)
        assertThat(errors).containsExactly("@AfterTable method <static ${methodPrefix}AfterTableWithParameter.afterTable(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `after table methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.NonStaticAfterTable::class)
        assertThat(errors).containsExactly("@AfterTable method <${methodPrefix}NonStaticAfterTable.afterTable()> must be static!")
    }

    private fun executeCheck(fixtureClass: KClass<*>): List<String> {
        val model = DecisionTableFixtureModel(fixtureClass.java)
        return DecisionTableFixtureChecker.check(model)
    }

}
