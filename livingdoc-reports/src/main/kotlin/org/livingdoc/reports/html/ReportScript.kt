package org.livingdoc.reports.html

private const val SCRIPT_CONTENT = """
            function collapse (indicator, row) {
                var indicatorElem = document.getElementById(indicator);
                var rowElem = document.getElementById(row);
                if (rowElem.classList.contains("hidden")) {
                    indicatorElem.innerHTML = "⏷";
                } else {
                    indicatorElem.innerHTML = "⏵";
                }
                rowElem.classList.toggle("hidden");
            }"""

fun reportScript(): String {
    return " ${SCRIPT_CONTENT.trimIndent()}"
}
