package org.livingdoc.results.groups

import org.livingdoc.results.Result
import org.livingdoc.results.documents.DocumentResult

/**
 * GroupResult represent the result of a whole group, even for Documents not explicitly in a Group. This result contains
 * a list of all DocumentResults.
 */
interface GroupResult : Result {
    val documentResults: List<DocumentResult>
}
