package org.livingdoc.reports.html.elements

class HtmlErrorContext {
    private var popupErrorNumber = 0
    val popupErrors = ArrayList<HtmlError>()

    fun getNextErrorNumber() = ++popupErrorNumber
    fun addPopupError(htmlError: HtmlError) = popupErrors.add(htmlError)
}

class HtmlError(val number: Int, val message: String, val stacktrace: String)
