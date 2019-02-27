@file:Suppress("MaxLineLength")
package org.livingdoc.engine.reporting

class HtmlReportTemplate {

    companion object {
        const val CSS_CLASS_BORDER_BLACK_ONEPX = "border-black-onepx"
        const val CSS_CLASS_ICON_FAILED = "icon-failed"
        const val CSS_CLASS_ICON_EXCEPTION = "icon-exception"
        const val CSS_CLASS_RESULT_VALUE = "result-value"
        const val HTML_HEAD_STYLE_CONTENT = """
            <style>
                .background-executed {background-color: #31fa01;}
                .background-skipped {background-color: #9d9d9d;}
                .background-unknown {background-color: white;}
                .background-failed {background-color: #ff9b00;}
                .background-exception {background-color: #ff0b00;}
                .$CSS_CLASS_BORDER_BLACK_ONEPX {border: 1px solid black;}
                .$CSS_CLASS_ICON_FAILED {
                    height: 24px;
                    width: 24px;
                    cursor: pointer;
                    background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAFjklEQVR42qVWaWxUVRT+znvzOm/amelealvaZtislFIE1ODSNoalphCU0E3QhEg08QckxkRiwn/9I4kBIwkYidAWgUgE41KlSlvSUNnElNCFrdsw7XSmnem82d71vJnSsirEm5x5d949537nO+fccx/hCcanlSDjqYWBnc0Qj2ND/7Z4qBqSLMFiUqXClGxbqSXZnkMkS9q4b9jr9F7S/OEeVvPXNkJ/IoD9G0DWBNiz8lE/dzG9kzV//mJJzZeRkMsWJoHwMImwSx/pvdLVc8779VAfvopGMVrX9CCrBwAa6yGZzViytIy+yC4wL5PS1kFKrgLUpwGd7YXO7kpAqBcYPwnd8x1GBye7zv4m3td8aK1uQOSRAIfqIFmtKH9pNTVYZ5VkmXI/AqR0QUInLSwhpOkIBYWIBiOUnglhksK8NIGIa7cIjJ6daPtRbPF7cHzjXSD3ABzdjIWvVNIv9twXspWcD4gMh3kQdHIO8m84YjAQkkyUlROJARuMDLWI+xvyO1u8v/8gqib9aKs/HA/XNEBDLZJKl+FYYVHBSnPhzqkl1iE9Nh3s5ScH2tjQYpORmh2Kze+EjT1ByLkfQz1d58+exsrqRrinAfasA+Vnoar8Vem4OudDktSnBEnsOG8uSGdTiQYus/d6FMSMkvNksqYHmYFBjnV03VAjPaoh0Pe5aP8j9K7bhX21TUw9FvsamBcW4eC8Jc9ssBTUQUisLfGaZABwzHWZBjqDkETMWzGr1ERKIgPo+hQAI0V5HhXQXOfFjQtnWi+cQ2XtYfjvAGSWrcCljJLXZympDkDW4yLxhgwSDsoYag+x9zpkk4ScCqPs+bTpUyGK6nGJCES5ELwXGwPNp1DMDK7FAJrqULzqZbpoW/q2JFtkCJkZmJg+gxjz4ISJnC3MgAtUzVJFYkmUtGEhUgrDJCMIpiiIc44Il7DkEBOn91BLu75y/QE0xwCObUJZxXI6ZS97C6T4AZM+JSLGJOBS4GrWIIt4wjnYMGcqSF8d4JApLHYmxHaaFwiMw9vRg/ZOrf50L5piAEfexIqKUmpNKVtOktkNoaqCEhIIJhPnQxL+WxZy/8wAXFUMwvGOkL1CEZYFHvZ6HCI0zO+YQdBIUZ7wtLio7UJwY9s1HI0BNNZgbnkxLqeVzzMr9m4IM+dWYV9lNuBdPZdUGvteR2K2BfoNr5AVmTJ3KEIya5xY1omyfoT1udCiwQXCc/IqTv0lXuQDdyYOUI2U0gK0zy4rKkqc02Ww5hywgSkOEA2rXLqcw9uAd5cmLIvtlFQ/zl7HN70bQBsoFsM/XXZ29OFZTvJQPMk1ULJt+KR4Ye721LUaV84ojM3vl3C/Cv8uDbatqZDnjSHWEO4WPQne5hxcOdfdeHMMW2qaoE2f5MO1KHk+B62Za5fZ1MLOGQamGTbBXhWhQyZh/TjCLUKb9lpE4gyCt5dj9NvOUEe/WOMPo2XzEX5/B6CpFua0BOxclGfbkb7ZAVPSxRkAOZ6PYJcKcT1ZqJVOioUlOgOgh4rIfdCFrusj+wb92FbHh+yBZtdQjQxHEg4U5GWtSa+fDdn6J4yNY8IdWvvbgoTsVEhpg4hdMdG46KESuJvc6O/r7+j2oZpDc+uh3XTvGyB7AvLyFewuSE2qSn5tESUucnK34APJmiGPA0rqTf4foVgTlfIocHU2vCe6xMCIp7UvgK2M283e6w8FMMaX60EpZqTZCe85FGyzZWZmJD7nILODo5RsZQsfXwFEwZsQkx03yDc46L0exl53FJ+FdTg3Hbn3+nzkncyVlcA+FiYTqtNVZZU9zTrfkmG3E58Bze2bGB+Z6B31BX4dE2hi9W6jYh77Tr4vLyaZYOGmYWdJMmzYaJItx3k+OeRDZPuJR39h/CfA/x3/AHnktafFlaFGAAAAAElFTkSuQmCC) no-repeat;
                    float: right;
                    padding-right: 5px;
                }
                .$CSS_CLASS_ICON_EXCEPTION {
                    height: 24px;
                    width: 24px;
                    cursor: pointer;
                    background: url(data:image/png;base64,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) no-repeat;
                    display: block;
                    margin: auto;
                }
                .$CSS_CLASS_RESULT_VALUE {
                    vertical-align: middle;
                }
                .overlay {
                    position: fixed;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    background: rgba(0, 0, 0, 0.7);
                    transition: opacity 500ms;
                    visibility: hidden;
                    opacity: 0;
                }
                .overlay:target {
                    visibility: visible;
                    opacity: 1;
                }
                .popup {
                    margin: 40px auto;
                    padding: 20px;
                    background: #fff;
                    border-radius: 5px;
                    min-width: 50%;
                    max-width: 80vw;
                    max-height: 80vh;
                    position: relative;
                    transition: all 500ms ease-in-out;
                    display: flex;
                    flex-direction: column;
                }
                .popup h2 {
                    margin-top: 0;
                    margin-right: 40px;
                    color: #333;
                    font-family: Tahoma, Arial, sans-serif;
                    word-wrap: break-word;
                    min-height: 35px;
                    max-height: 60px;
                    overflow: auto;
                }
                .popup .close {
                    position: absolute;
                    top: 20px;
                    right: 30px;
                    transition: all 200ms;
                    font-size: 30px;
                    font-weight: bold;
                    text-decoration: none;
                    color: #333;
                }
                .popup .close:hover {
                    color: #06D85F;
                }
                .popup .content {
                    overflow: auto;
                }
                table {
                    border-collapse: collapse;
                    margin: 15px;
                }
                th, td {
                    min-width: 30px;
                    height: 30px;
                    text-align: center;
                }
                th {
                    background-color: steelblue;
                    color: white;
                }
            </style>
        """
    }

    fun renderTemplate(
        htmlResults: List<HtmlResult>,
        renderContext: HtmlRenderContext
    ): String {
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    ${HTML_HEAD_STYLE_CONTENT.trimIndent()}
                </head>
                <body>
                    ${htmlResults.joinToString(separator = "\n")}
                    ${createErrorPopups(renderContext)}
                </body>
            </html>
            """.trimIndent()
    }

    private fun createErrorPopups(renderContext: HtmlRenderContext): String {
        return renderContext.popupErrors.joinToString(separator = "") { error ->
            """

            <div id="popup${error.number}" class="overlay">
                <div class="popup">
                    <h2>${error.message}</h2>
                    <a class="close" href="#">&times;</a>
                    <div class="content">
                        <pre>
                            ${error.stacktrace}
                        </pre>
                    </div>
                </div>
            </div>
            """.trimIndent()
        }
    }
}
