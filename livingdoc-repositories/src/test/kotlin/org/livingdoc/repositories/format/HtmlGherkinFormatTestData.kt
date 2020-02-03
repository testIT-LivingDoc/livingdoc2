package org.livingdoc.repositories.format

object HtmlGherkinFormatTestData {

    fun emptyHtml() = """
    <!DOCTYPE html>
    </html>
            """.byteInputStream()

    fun getHtmlGherkinTableWithOnlyOneRow() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <pre>
            <gherkin>
            Feature: Test Feature
                Scenario: Test Scenario
                    When I test the Gherkin parser
            </gherkin>
        </pre>
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

    fun htmlGherkinGiven() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <pre>
            <gherkin>
            Feature: Test Feature
                Scenario: Test Scenario
                    Given I test the Gherkin parser
            </gherkin>
        </pre>
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

    fun htmlGherkinThen() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <pre>
            <gherkin>
            Feature: Test Feature
                Scenario: Test Scenario
                    Then I test the Gherkin parser
            </gherkin>
        </pre>
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

    fun getHtmlGherkin2() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <pre>
        <gherkin>
        Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
        <gherkin>
        Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
    </pre>
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

    fun getHtmlGherkinDescriptionText() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <p>This is a descriptive text.</p>
        <ol>
            <li>First list item</li>
            <li>Second list item</li>
        </ol>
        <p>This is another descriptive text.</p>
    </body>
    </html>
    """.byteInputStream()
}
