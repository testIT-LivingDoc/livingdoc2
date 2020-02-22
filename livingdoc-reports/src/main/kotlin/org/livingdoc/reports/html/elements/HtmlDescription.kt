package org.livingdoc.reports.html.elements

class HtmlDescription(block: HtmlDescription.() -> Unit) : HtmlElement("div") {
    init {
        block()
    }
}

/**
 * Adds the given paragraphs to the [HtmlDescription] element
 *
 * @param paragraphs A list of strings with each entry representing a paragraph
 */
fun HtmlDescription.paragraphs(paragraphs: List<String>) {
    paragraphs.forEach { paragraph ->
        if (paragraph.isNotEmpty())
            appendChild {
                HtmlElement("p", paragraph)
            }
    }
}
