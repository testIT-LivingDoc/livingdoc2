package org.livingdoc.repositories.format

object HtmlGherkinFormatTestData {

    fun getHtmlGherkinTableWithOnlyOneRow() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <div>
     Feature: Test Feature
          Scenario: Test Scenario 1
            Given a working Gherkin parser
            And some Gherkin text
            When I test the Gherkin parser
            Then I get a valid Document containing the expected information
     </div>
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
}
