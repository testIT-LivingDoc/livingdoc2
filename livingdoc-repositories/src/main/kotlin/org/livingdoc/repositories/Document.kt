package org.livingdoc.repositories

open class Document(val tables: List<DecisionTable>, val lists: List<Scenario>)

abstract class ResultItem(val result: Result = Result.Unknown)

data class DecisionTable(val headers: List<String>, val rows: List<DecisionTableRow>) : ResultItem()

data class DecisionTableRow(val cells: Map<String, DecisionTableCell>)

data class DecisionTableCell(val text: String) : ResultItem()

data class Scenario(val steps: List<String>) : ResultItem()

sealed class Result {
    /** Nothing is known about the result state. */
    object Unknown : Result()

    /** Execution was skipped. */
    object Skipped : Result()

    /** Successfully executed. */
    object Success : Result()

    /** A validation failed with a reason. */
    data class Failure(val reason: String) : Result()

    /** An unexpected exception occurred. */
    data class Exception(val exception: Throwable) : Result()
}

