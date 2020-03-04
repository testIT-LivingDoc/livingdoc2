package org.livingdoc.reports.html.elements

import org.livingdoc.reports.html.MILLISECONDS_DIVIDER
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.scenarios.StepResult
import java.nio.file.Path

/**
 * A list element in a HTML report
 *
 * @param block A lambda filling the list
 */
class HtmlList(block: HtmlList.() -> Unit) : HtmlElement("ul") {

    init {
        block()
    }
}

/**
 * Fills a list with the given [scenario step results][StepResult]
 *
 * @param stepResults A list of [step results][StepResult]
 */
fun HtmlList.steps(stepResults: List<StepResult>) {
    stepResults.forEach { (value, result) ->
        child {
            HtmlElement("li") {
                cssClass(determineCssClassForBackgroundColor(result))
                text { value }
            }
        }
    }
}

/**
 * Fills a list with links to the given HTML reports
 *
 * @param reports A list of pairs matching a [DocumentResult] to the path of its HTML report
 */
fun HtmlList.linkList(reports: List<Pair<DocumentResult, Path>>) {
    reports.map {
        child {
            HtmlElement("li") {
                child {
                    HtmlLink(it.second.fileName.toString()) {

                        resultLink(
                            it.first.documentClass.name + " (" +
                                    "%.3f".format(it.first.time.toMillis() / MILLISECONDS_DIVIDER) + "s)",
                            getLinkStatus(it.first)
                        )
                    }
                }
            }
        }
    }
}
