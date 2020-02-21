package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.results.Status

interface HtmlResult

internal fun Element.setStyleClasses(vararg classes: String) {
    this.attr("class", classes.joinToString(separator = " "))
}

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
