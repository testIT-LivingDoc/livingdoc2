package org.livingdoc.repositories.format

object HtmlFormatTestData {

    fun getHtmlTableWithOnlyOneRow() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table style="width:100%">
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Age</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()


    fun getHtmlTableWithNonUniqueHeaders() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table>
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Lastname</th>
            </tr>
            <tr>
                <td>Jill</th>
                <td>Thomsen</th>
                <td>35</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()


    fun getHtmlTableWithWrongCellCount() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table>
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Age</th>
            </tr>
            <tr>
                <td>Jill</th>
                <td>Thomsen</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()


    fun getValidHtml() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>LivingDoc-HTML Parser</title>
    </head>
    <body>
    <h1>Headline First Order</h1>

    SIMPLE TEXT

    <p>
        PARAGRAPH CONTENT
    </p>
    <div>CONTENT OF THE DIV</div>

    <ul>
        <li>
            text1
            <p>
                List Paragraph
            </p>
            <table style="width:100%">
                <tr>
                    <th>Firstname</th>
                    <th>Lastname</th>
                    <th>Age</th>
                </tr>
                <tr>
                    <td>Jill</td>
                    <td>Smith</td>
                    <td>50</td>
                </tr>
                <tr>
                    <td>Eve</td>
                    <td>Jackson</td>
                    <td>94</td>
                </tr>
            </table>
            text111
        </li>
        <li>text2</li>
        <li>text3</li>
    </ul>

    <ol>
        <li>text4</li>
        <li>text5</li>
        <li>text6</li>
    </ol>

    <ol type = "">
        <li>text7</li>
        <li>text8</li>
        <li>text9</li>
    </ol>

    <ol type = "A">
        <li>textX</li>
        <li>textY</li>
        <li>textZ</li>
    </ol>

    </body>
    </html>
    """.byteInputStream()


    fun getHtmlWithUnorderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ul>
            <li>First list item</li>
            <li>Second list item</li>
            <li>Third list item</li>
            <li>Fourth list item</li>
            <li>Fifth list item</li>
        </ul>
    </body>
    </html>
            """.byteInputStream()


    fun getHtmlWithOrderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ol>
            <li>First list item</li>
            <li>Second list item</li>
            <li>Third list item</li>
            <li>Fourth list item</li>
            <li>Fifth list item</li>
        </ol>
    </body>
    </html>
            """.byteInputStream()


    fun getHtmlWithUnorderedListContainsOnlyOneItem() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ul>
            <li>First list item</li>
        </ul>
    </body>
    </html>
            """.byteInputStream()


    fun getHtmlWithOrderedListContainsOnlyOneItem() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ol>
            <li>First list item</li>
        </ol>
    </body>
    </html>
    """.byteInputStream()


    fun getHtmlUnorderedListWithNestedUnorderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ul>
            <li>First list item</li>
            <li>Second list item
                <ul>
                    <li>First nested item</li>
                </ul>
            </li>
        </ul>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlUnorderedListWithNestedOrderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ul>
            <li>First list item</li>
            <li>Second list item
                <ol>
                    <li>First nested item</li>
                </ol>
            </li>
        </ul>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlOrderedListWithNestedUnorderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ol>
            <li>First list item</li>
            <li>Second list item
                <ul>
                    <li>First nested item</li>
                </ul>
            </li>
        </ol>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlOrderedListWithNestedOrderedList() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <ol>
            <li>First list item</li>
            <li>Second list item
                <ol>
                    <li>First nested item</li>
                </ol>
            </li>
        </ol>
    </body>
    </html>
    """.byteInputStream()
}