package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element

/**
 * This class represents any HTML element to be used in an HTML report
 *
 * @param tag Defines the tag that specifies this HTML element
 */
open class HtmlElement(tag: String) {
    protected val element = Element(tag)

    constructor(tag: String, value: String) : this(tag) {
        element.html(value)
    }

    /**
     * This class represents any HTML element to be used in an HTML report
     *
     * @param tag Defines the tag that specifies this HTML element
     * @param block A lambda that should set the content and attributes of this [HtmlElement]
     */
    constructor(tag: String, block: HtmlElement.() -> Unit) : this(tag) {
        block()
    }

    override fun toString(): String {
        return if (element.hasText()) element.outerHtml() else ""
    }

    /**
     * Appends a new child element to this [HtmlElement]
     *
     * @param child A lambda returning the new child element
     */
    fun child(child: () -> HtmlElement) {
        element.appendChild(child().element)
    }

    /**
     * Appends the given text to this [HtmlElement]
     *
     * @param text A lambda returning the text
     */
    fun text(text: () -> String) {
        element.append(text())
    }

    /**
     * Adds a new css class to this [HtmlElement]
     *
     * @param cl A [String] representing a css class
     */
    fun cssClass(cl: String) {
        element.addClass(cl)
    }

    /**
     * Sets a given attribute in this [HtmlElement] with the given value
     *
     * @param key The name of the attribute
     * @param value The value to assign to the attribute
     */
    fun attr(key: String, value: String) {
        element.attr(key, value)
    }
}
