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
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Thread)
open class DecisionTableExecutionBenchmarks {
    lateinit var table: DecisionTable
    
    @Setup(Level.Iteration)
    fun generateRandomTestTable() {
        val headers = listOf(Header("a"), Header("b"), Header("a + b = ?"))

        val rows = (0..9_999).map {
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
    fun baseline() {
    }

    @Benchmark
    fun simpleDecisionTableBenchmark(): DecisionTableResult {
        return DecisionTableFixtureWrapper(DecisionTableSequential::class.java).execute(table)
    }
}

@DecisionTableFixture
class DecisionTableSequential {
    @Input("a")
    var a: Long = 0

    @Input("b")
    var b: Long = 0

    @Check("a + b = ?")
    fun `sum is correct`(expected: Long) {
        Assertions.assertThat(a + b).isEqualTo(expected)
    }
}