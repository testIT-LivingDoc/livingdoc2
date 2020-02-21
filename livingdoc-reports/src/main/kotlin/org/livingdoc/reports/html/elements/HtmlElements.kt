package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.reports.html.HtmlReportTemplate
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.RowResult
import org.livingdoc.results.examples.scenarios.StepResult
import java.io.PrintWriter
import java.io.StringWriter

class HtmlRenderContext {
    private var popupErrorNumber = 0
    val popupErrors = ArrayList<HtmlError>()

    fun getNextErrorNumber() = ++popupErrorNumber
    fun addPopupError(htmlError: HtmlError) = popupErrors.add(htmlError)
}

class HtmlError(val number: Int, val message: String, val stacktrace: String)
