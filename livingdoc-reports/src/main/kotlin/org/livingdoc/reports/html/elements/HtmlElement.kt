package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element

open class HtmlElement(tag: String): HtmlResult {
    protected val element = Element(tag)

    constructor(tag: String, value: String): this(tag) {
        element.html(value)
    }

    constructor(tag: String, block: HtmlElement.() -> Unit): this(tag) {
        block()
    }

    override fun toString(): String {
        return if (element.hasText()) element.toString() else ""
    }

    fun appendChild(child: () -> HtmlElement) {
        element.appendChild(child().element)
    }

    fun appendHtml(html: () -> String) {
        element.append(html())
    }

    fun addClass(cl: () -> String) {
        element.addClass(cl())
    }

    fun setAttr(key: String, value: () -> String) {
        element.attr(key, value())
    }
}
