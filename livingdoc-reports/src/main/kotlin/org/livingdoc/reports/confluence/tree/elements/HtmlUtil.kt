package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.results.Status

/**
 * Determines the confluence css styles that are associated with the given status and returns it
 *
 * @param status A [result status][Status]
 * @returns A [String] representing css styles
 */
internal fun determineCfStylesForStatus(status: Status): String {
    return when (status) {
        Status.Executed -> "color: rgb(0, 128, 0);"
        is Status.Disabled -> "color: rgb(165, 173, 186);"
        Status.Manual -> "color: rgb(255, 102, 0);"
        Status.Skipped -> "color: rgb(165, 173, 186);"
        Status.Unknown -> "color: rgb(165, 173, 186);"
        is Status.ReportActualResult -> "color: rgb(69, 140, 255);"
        is Status.Failed -> "color: rgb(255, 0, 0);"
        is Status.Exception -> "color: rgb(255, 0, 0);"
    }
}

/**
 * Determines the confluence css class that is associated with the given status and returns it
 *
 * @param status A [result status][Status]
 * @returns A [String] representing css styles
 */
internal fun determineCfClassForStatus(status: Status): String {
    return when (status) {
        Status.Executed -> "highlight-green"
        is Status.Disabled -> "highlight-grey"
        Status.Manual -> "highlight-yellow"
        Status.Skipped -> "highlight-grey"
        Status.Unknown -> "highlight-grey"
        is Status.ReportActualResult -> "highlight-blue"
        is Status.Failed -> "highlight-red"
        is Status.Exception -> "highlight-red"
    }
}
