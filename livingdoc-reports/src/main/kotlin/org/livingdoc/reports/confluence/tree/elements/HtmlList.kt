package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.scenarios.StepResult

/**
 * Fills a list with the given [scenario step results][StepResult]
 *
 * @param stepResults A list of [step results][StepResult]
 */
fun HtmlList.cfSteps(stepResults: List<StepResult>) {
    stepResults.forEach { (value, result) ->
        child {
            HtmlElement("li") {
                attr("style", determineCfStylesForStatus(result))
                text { value }
            }
        }
    }
}

fun HtmlList.cfLinkList(reports: List<DocumentResult>) {
    reports.map { report ->
        child {
            HtmlElement("li") {
                child {
                    ConfluenceLink(report)
                }
            }
        }
    }
}
