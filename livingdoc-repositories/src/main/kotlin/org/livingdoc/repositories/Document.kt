package org.livingdoc.repositories

class Document(val content: List<DocumentNode>)

sealed class DocumentNode {
    var result: Result = Result.Unknown
    abstract val children: List<DocumentNode>

    data class Text(
            val text: String,
            override val children: List<DocumentNode>
    ) : DocumentNode()

    data class TextList (
            val items: List<Text>,
            override val children: List<DocumentNode>
    ) : DocumentNode()

    data class Table(
            val rows: List<TableRow>,
            override val children: List<DocumentNode>
    ) : DocumentNode()

    data class TableRow(
            val cells: List<TableCell>,
            override val children: List<DocumentNode>
    ) : DocumentNode()

    data class TableCell(
            val text: String,
            override val children: List<DocumentNode>
    ) : DocumentNode()
}


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

