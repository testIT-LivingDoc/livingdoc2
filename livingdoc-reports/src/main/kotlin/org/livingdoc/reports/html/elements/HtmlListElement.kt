package org.livingdoc.reports.html.elements

class HtmlListElement : HtmlElement {
    constructor(value: String) : super("li", value)

    constructor(block: HtmlListElement.() -> Unit) : super("li") {
        block()
    }
}
