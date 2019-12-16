package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegMatchingTest {

    fun valueProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                "adding 10 and 10 and 10 and 10 and 10 and 10 and 100000000000000000000000 equals 100000000000000000000060",
                0.0f
            ),
            Arguments.of("adding 10 and 10 and 10 and 10 and 10 and 10 and 10 equals 70", 0.0f)
        )
    }

    @ParameterizedTest()
    @MethodSource("valueProvider")
    fun `long input with mismatch`(step: String, cost: Float) {
        val template = "adding {a} and {b} equals {c}"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
    }

    @Test
    fun `multiple templates for one case`() {

        val step1 = "adding 1234 and 234 and 345 equals 0"
        val template1 = "adding {a} and {b} equals {c}"
        val template2 = "adding {a} and {b} and {c} equals {d}"
        val sp1 = StepTemplate.parse(template1)
        val sp2 = StepTemplate.parse(template2)
        val stm = ScenarioStepMatcher(listOf(sp1, sp2)).match(step1)
        Assertions.assertThat(stm.variables)
            .isEqualTo(mapOf(("a" to "1234"), ("b" to "234"), ("c" to "345"), ("d" to "0")))
    }

    @Test
    fun `longest input 1000`() {
        val template = "adding {a} and {b} equals {c}"
        val sp = StepTemplate.parse(template)
        val step = "adding 1000000000000000000000000000 and 200000000000000 and 1239912931 " +
                "and 129391293 and 12931729847129874198273918237981237198237198237918273" +
                "and 12391241923913 and 1892371892471289739182731927319823719824617283619237" +
                "and 129391293192391 and 129391293192391 " +
                "and 123919239129312731923718279187391823717246 " +
                "and 12936187312836178238123678134572935123 " +
                "equals 12039120391489283"
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(115.0f)
    }

    @Test
    fun `shoppinglist with many items`() {
        val template = "adding {a} to the cart"
        val sp = StepTemplate.parse(template)
        val step = "adding 1000000000000000000000000000 " +
                "and 200000000000000 " +
                "and 1239912931 " +
                "and 129391293 " +
                "and 12931729847129874198273918237981237198237198237918273" +
                "and 12391241923913 " +
                "and 1892371892471289739182731927319823719824617283619237" +
                "and 129391293192391 " +
                "and 129391293192391 " +
                "and 123919239129312731923718279187391823717246 " +
                "and 12936187312836178238123678134572935123 " +
                "equals 12039120391489283 " +
                "and 102931409840129830138 " +
                "and and and oranges" +
                "and apples" +
                "and a pie" +
                "and bananas" +
                "and popcorn " +
                "to the cart"
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(451.0f)
    }

    @Test
    fun `many variables and long input match`() {
        val template = "adding {a} and {b} and {c} and {d} and {e} and {f} and {g} equals {h}"
        val sp = StepTemplate.parse(template)
        val step = "adding 012973981234567890128376437829108376343289 " +
                "and 516278398476743892012384754382993876748392038437433 " +
                "and 34672898234738290238347389203847839203847839208478392 " +
                "and 26378934754839203984748329834754839284375 " +
                "and 4367289823478392084783984754839284789308475 " +
                "and 64378289734647382938478392847839284783948 " +
                "and 34678237465347829834743829847543892038483902938 " +
                "equals 0"
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(37.0f)
    }

    @Test
    fun `upper limit for strings in single variable misaligned`() {
        val template = "adding {a}"
        val step = "adding 10 and 10 and 10 and 10 and 10 and 10 and 10"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(44.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `upper limit for strings in single variable successful`() {
        val template = "adding {a}"
        val step = "adding 10 and 10 and 10 and 10 and 10 and 10 and 10"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(44.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @ParameterizedTest
    @MethodSource("valueProvider")
    fun `long input text test`(step: String, cost: Float) {
        val template = "adding {a} and {b} and {c} and {d} and {e} and {f} and {g} equals {h}"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(cost)
    }

    @ParameterizedTest
    @ValueSource(
        strings = arrayOf(
            "adding 1111 and 02222 equals 03333",
            "adding 1111 and 2222 equals 03333",
            "adding 1111 and 2222 equals 3333",
            "adding 12345678 and 98765432 equals 111111110",
            "adding 11111 and 22222 equals 33333",
            "adding 01111 and 02222 equals 03333",
            "adding 012319 and 01239104 equals 1251423"
        )
    )
    fun `test inputs to match to a template`(step: String) {
        val template = "adding {a} and {b} equals {c}"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(0.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `mismatched test`() {
        val template = "understandable text here is not understanable at {all}"
        val step = "unnasdjiaosd aiojdoaij aisodja"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 4.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(4.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(true)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(4.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(4.0f)
        Assertions.assertThat(regm.variables).isEmpty()
    }

    @Test
    fun `value a or an in string`() {
        val template = "I have a {a}"
        val step = "I have an apple"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(2.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(6.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `test plural`() {
        val template = "I have a {a} and inputs and a {b}"
        val step = "I have a apple and input and a bulletpoint"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(1.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(8.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `more complex test with a or an`() {
        val template = "I have a {a} and a input and a {b}"
        val step = "I have an apple and an input and an bulletpoint"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(sp, step, 3.0f)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3.0f)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost.first).isEqualTo(2.0f)
        Assertions.assertThat(regm.totalCost.second).isEqualTo(9.0f)
        Assertions.assertThat(regm.variables).isNotNull
    }
}
