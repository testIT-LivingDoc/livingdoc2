package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RegMatchingTest {
    @Test
    fun `mismatched test`() {
        val template = "understandable text here is not understanable at {all}"
        val step = "unnasdjiaosd aiojdoaij aisodja"
        val sp = StepTemplate.parse(template)
        val regm = RegMatching(StepTemplate.parse(template), step, 15)
        Assertions.assertThat(regm.stepTemplate.toString()).isEqualTo(sp.toString())
        Assertions.assertThat(regm.step).isEqualTo(step)
        Assertions.assertThat(regm.maxCost).isEqualTo(15)
    }
}
