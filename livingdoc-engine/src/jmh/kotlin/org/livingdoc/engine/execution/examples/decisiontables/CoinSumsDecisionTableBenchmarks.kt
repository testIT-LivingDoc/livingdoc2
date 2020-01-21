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
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class CoinSumsDecisionTableBenchmarks {
    lateinit var table: DecisionTable

    @Setup(Level.Trial)
    fun generateTable() {
        val headers = listOf(Header("sum"), Header("coinSums(sum) = ?"))

        val rows = (0 until 10).map {
            Row(
                mapOf(
                    headers[0] to Field("250"),
                    headers[1] to Field("0")
                )
            )
        }

        table = DecisionTable(headers, rows)
    }

    @Benchmark
    fun sequentialCoinSums(): DecisionTableResult {
        return DecisionTableFixtureWrapper(SequentialCoinSums::class.java).execute(table)
    }

    @Benchmark
    fun parallelCoinSums(): DecisionTableResult {
        return DecisionTableFixtureWrapper(ParallelCoinSums::class.java).execute(table)
    }
}

val coins = listOf(1L, 2, 5, 10, 20, 50, 100, 200)

fun coinSums(sum: Long, lastCoin: Long = 200): Long {
    return if (sum <= 0L) {
        1
    } else {
        coins.filter { coin ->
            coin <= lastCoin && coin <= sum
        }.map { coin ->
            coinSums(sum - coin, coin)
        }.sum()
    }
}

@DecisionTableFixture
class SequentialCoinSums {
    @Input("sum")
    var sum: Long = 0

    @Check("coinSums(sum) = ?")
    fun `coinSums is correct`(expected: Long) {
        Assertions.assertThat(coinSums(sum)).isEqualTo(expected)
    }
}

@DecisionTableFixture(parallel = true)
class ParallelCoinSums {
    @Input("sum")
    var sum: Long = 0

    @Check("coinSums(sum) = ?")
    fun `coinSums is correct`(expected: Long) {
        Assertions.assertThat(coinSums(sum)).isEqualTo(expected)
    }
}
