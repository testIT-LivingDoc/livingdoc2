@file:Suppress("MaxLineLength")
package org.livingdoc.reports.html

private const val CSS_CLASS_BORDER_BLACK_ONEPX = "border-black-onepx"
        private const val CSS_CLASS_ICON_FAILED = "icon-failed"
        private const val CSS_CLASS_ICON_EXCEPTION = "icon-exception"
        private const val CSS_CLASS_RESULT_VALUE = "result-value"
        private const val STYLE_CONTENT = """
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
            li.background-executed, a.background-executed {color: rgb(0,128,0);}
            li.background-disabled, a.background-disabled {color: #5e6c84;}
            li.background-manual, a.background-manual {color: rgb(255,102,0);}
            li.background-skipped, a.background-skipped {color: #5e6c84;}
            li.background-unknown, a.background-unknown {color: #5e6c84;}
            li.background-report-result, a.background-report-result {color: #458cff;}
            li.background-failed, a.background-failed {color: rgb(255,0,0);}
            li.background-exception, a.background-exception {color: rgb(255,0,0);}

            .successfulCell, td.background-executed {background-color: #e3fcef;}
            td.background-disabled {background-color: #f4f5f7;}
            td.background-manual {background-color: #fffae5;}
            td.background-skipped {background-color: #f4f5f7;}
            .otherCell, td.background-unknown {background-color: #fffae5;}
            td.background-report-result {background-color: #deebff;}
            .failedCell, td.background-failed {background-color: #ffebe5;}
            td.background-exception {background-color: #ffebe5;}

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

            html {
                height: 100%;
            }


            body {
                display: flex;
                flex-direction: column;
                height: 100%;
                margin: 0;
                font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,
                Oxygen,Ubuntu,Fira Sans,Droid Sans,Helvetica Neue,sans-serif;
                color: #172b4d;
                font-size: 14px;
                font-weight: 400;
                line-height: 1.42857143;
                letter-spacing: 0;
            }


            .flex {
                display: flex;
                flex: 1 0 auto;
                flex-direction: column;
            }
            .column {
                overflow: auto;
                padding-left: 40px;
                padding-right: 40px;
                padding-bottom: 40px;
                overflow-wrap: break-word;
            }

            @media (min-width: 1000px) {
             .flex-row {
                 flex-direction: row !important;
             }
             .flex-50 {
                 width: 50%;
             }
            }
            @media (max-width: 999px) {
             .flex {
                 flex-direction: column-reverse;
             }
            }

            .footer {
                flex-shrink: 0;
                background-color: #f4f5f7;
                padding: 0px 40px;
                color: #7a869a;
                font-size: 12px;
                line-height: 1.66666667;
                text-align: center;
            }



            table {
                border-collapse: collapse;
            }

            th, td {
                min-width: 30px;
                text-align: center;
                border: 1px solid #c1c7d0;
                padding: 7px 5px;
            }
            th {
                background: #f4f5f7 center right no-repeat;
                color: #172B4D;
                font-weight: bold;
            }





            .hidden {
                display: none;
            }

            #summary-table {
                width: 90%;
                border-width: 1px;
            }
            #summary-table th:first-child, #summary-table td:first-child {
                text-align: left;
                min-width: 50px;
            }
            #summary-table th:nth-child(1n+2), #summary-table td:nth-child(1n+2) {
                width: 50px;
            }
            #summary-table ul {
                padding-left: 25px;
                padding-right: 25px;
            }

            .indicator {
                cursor: pointer;
            }
            .tag-cell {
                user-select: none;
            }


            h2 {
                color: #172B4D;
                text-decoration: none;
                font-size: 28px;
                line-height: 1.25;
                font-weight: 500;
                text-transform: none;
                letter-spacing: -.01em;
            }

            a {
                text-decoration: none;
            }
            a:hover {
                text-decoration: underline;
            }


            .tag {
                min-width: 76px;
                background: #42526e;
                background-color: rgb(66, 82, 110);
                border: 0 solid #42526e;
                border-top-color: rgb(66, 82, 110);
                border-right-color: rgb(66, 82, 110);
                border-bottom-color: rgb(66, 82, 110);
                border-left-color: rgb(66, 82, 110);
                border-radius: 3px;
                color: #fff;
                display: inline-block;
                font-size: 11px;
                font-weight: 700;
                line-height: 1;
                margin: 0;
                margin-right: 5px;
                padding: 2px 5px 3px;
                text-align: center;
                text-decoration: none;
                text-transform: uppercase;
                background-color: #0052cc;
                border-color: #0052cc;
                color: #fff;
            }
        """

fun reportStyle(): String {
    return " ${STYLE_CONTENT.trimIndent()}"
}
