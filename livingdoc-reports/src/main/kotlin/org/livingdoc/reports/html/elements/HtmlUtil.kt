package org.livingdoc.reports.html.elements

import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult

/**
 * Determines the css class that is associated with the given status and returns it
 *
 * @param status A [result status][Status]
 * @returns A [String] representing a css class
 */
internal fun determineCssClassForBackgroundColor(status: Status): String {
    return when (status) {
        Status.Executed -> "background-executed"
        is Status.Disabled -> "background-disabled"
        Status.Manual -> "background-manual"
        Status.Skipped -> "background-skipped"
        Status.Unknown -> "background-unknown"
        is Status.ReportActualResult -> "background-report-result"
        is Status.Failed -> "background-failed"
        is Status.Exception -> "background-exception"
    }
}

/**
 * Checks if a document contains a failed or exception status in its test data results
 *
 * @param document the [document][DocumentResult] that should be investigated
 * @return [Status.Failed] if the document contains a fail or exception, otherwise [Status.Unknown]
 */
fun checkFailedStatus(document: DocumentResult): Any {
    var status: Status =
        Status.Unknown
    if (document.results.any { testDataResult ->
            when (testDataResult) {
                is ScenarioResult -> {
                    checkScenarioResultStatus(testDataResult)
                }
                is DecisionTableResult -> {
                    checkDecisionTableResultStatus(testDataResult)
                }
                else ->
                    false
            }
        })
        status = Status.Failed(AssertionError(""))
    return status
}

private fun checkScenarioResultStatus(testDataResult: ScenarioResult): Boolean {
    return testDataResult.status is Status.Failed ||
            testDataResult.status is Status.Exception ||
            testDataResult.steps.any { step ->
                step.status is Status.Failed || step.status is Status.Exception
            }
}

private fun checkDecisionTableResultStatus(testDataResult: DecisionTableResult): Boolean {
    return testDataResult.status is Status.Failed ||
            testDataResult.status is Status.Exception ||
            testDataResult.rows.any { row ->
                row.status is Status.Failed ||
                        row.status is Status.Exception ||
                        row.headerToField.any { (_, field) ->
                            field.status is Status.Failed || field.status is Status.Exception
                        }
            }
}

/**
 * This method returns the [Status] that should be used for link coloring in link lists.
 * @param document the [document][DocumentResult] that should be investigated
 * @return [Status.Failed] if the document contains a fail or exception, otherwise the Status of the document
 */
fun getLinkStatus(document: DocumentResult): Status {
    val contentStatus = checkFailedStatus(document)
    if (contentStatus is Status.Failed)
        return contentStatus
    else
        return document.documentStatus
}
