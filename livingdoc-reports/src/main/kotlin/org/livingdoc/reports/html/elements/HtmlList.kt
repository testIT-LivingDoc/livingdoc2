package org.livingdoc.reports.html.elements

import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.scenarios.StepResult
import java.nio.file.Path

class HtmlList(block: HtmlList.() -> Unit) : HtmlElement("ul") {

    init {
        block()
    }
}

fun HtmlList.steps(stepResults: List<StepResult>) {
    stepResults.forEach { (value, result) ->
        appendChild {
            HtmlListElement {
                appendHtml { value }
                addClass(determineCssClassForBackgroundColor(result))
            }
        }
    }
}

fun HtmlList.linkList(reports: List<Pair<DocumentResult, Path>>) {
    reports.map {
        appendChild {
            HtmlListElement {
                HtmlLink(it.second.fileName.toString(), it.first.documentStatus) {
                    it.first.documentClass.name
                }
            }
        }
    }
}
