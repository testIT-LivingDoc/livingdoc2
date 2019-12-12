package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RegMatchingTest {

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
        "adding 10 and 10 and 10 and 10 and 10 and 10 and 100000000000000000000000 equals 100000000000000000000060",
        "adding 10 and 10 and 10 and 10 and 10 and 10 and 10 equals 70"))
    fun `long input text test`(step: String) {
        val template = "adding {a} and {b} and {c} and {d} and {e} and {f} and {g} equals {h}"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 3)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost).isEqualTo(0)
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf("adding 1111 and 02222 equals 03333",
        "adding 1111 and 2222 equals 03333",
        "adding 1111 and 2222 equals 3333",
        "adding 12345678 and 98765432 equals 111111110",
        "adding 11111 and 22222 equals 33333",
        "adding 01111 and 02222 equals 03333",
        "adding 012319 and 01239104 equals 1251423")
    )
    fun `test inputs to match to a template`(step: String) {
        val template = "adding {a} and {b} equals {c}"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 3)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost).isEqualTo(0)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `mismatched test`() {
        val template = "understandable text here is not understanable at {all}"
        val step = "unnasdjiaosd aiojdoaij aisodja"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 4)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(4)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(true)
        Assertions.assertThat(regm.totalCost).isEqualTo(4)
        Assertions.assertThat(regm.variables).isEmpty()
    }

    @Test
    fun `value a or an in string`() {
        val template = "I have a {a}"
        val step = "I have an apple"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 3)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost).isEqualTo(2)
        Assertions.assertThat(regm.variables).isNotNull
    }

    @Test
    fun `more complex test with a or an`() {
        val template = "I have a {a} and a input and a {b}"
        val step = "I have an apple and an input and an bulletpoint"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 3)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxNumberOfOperations).isEqualTo(3)
        Assertions.assertThat(regm.isMisaligned()).isEqualTo(false)
        Assertions.assertThat(regm.totalCost).isEqualTo(2)
        Assertions.assertThat(regm.variables).isNotNull
    }
}
