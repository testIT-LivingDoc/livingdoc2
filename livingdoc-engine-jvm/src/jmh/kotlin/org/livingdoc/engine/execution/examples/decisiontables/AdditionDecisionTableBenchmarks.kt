package org.livingdoc.engine.execution.examples.decisiontables

import org.assertj.core.api.Assertions
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Thread)
open class AdditionDecisionTableBenchmarks {
    lateinit var table: DecisionTable

    @Param("1", "10", "100", "1000", "10000", "100000", "1000000")
    var tableSize: Int = 0

    @Setup(Level.Iteration)
    fun generateRandomAdditionTable() {
        val headers = listOf(Header("a"), Header("b"), Header("a + b = ?"))

        val rows = (0 until tableSize).map {
            val a = Random.nextLong(Long.MAX_VALUE)
            val b = Random.nextLong(Long.MAX_VALUE - a)
            val result = a + b

            Row(
                mapOf(
                    headers[0] to Field(a.toString()),
                    headers[1] to Field(b.toString()),
                    headers[2] to Field(result.toString())
                )
            )
        }

        table = DecisionTable(headers, rows)
    }

    @Benchmark
    fun sequentialAddition(): DecisionTableResult {
        return DecisionTableFixtureWrapper(SequentialAddition::class.java).execute(table)
    }

    @Benchmark
    fun parallelAddition(): DecisionTableResult {
        return DecisionTableFixtureWrapper(ParallelAddition::class.java).execute(table)
    }
}

@DecisionTableFixture
class SequentialAddition {
    @Input("a")
    var a: Long = 0

    @Input("b")
    var b: Long = 0

    @Check("a + b = ?")
    fun `sum is correct`(expected: Long) {
        Assertions.assertThat(a + b).isEqualTo(expected)
    }
}

@DecisionTableFixture(parallel = true)
class ParallelAddition {
    @Input("a")
    var a: Long = 0

    @Input("b")
    var b: Long = 0

    @Check("a + b = ?")
    fun `sum is correct`(expected: Long) {
        Assertions.assertThat(a + b).isEqualTo(expected)
    }
}
