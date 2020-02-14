package org.livingdoc.repositories.format

object HtmlGherkinFormatTestData {

    fun getDescriptiveHtml() = """
    <!DOCTYPE html>
    <body>
        <pre>
            <gherkin>
                Feature: Test Feature
                descriptive text is here.
                    Scenario: Test Scenario
                        When I test the Gherkin parser
            </gherkin>
        </pre>

    </body>
    </html>
    """.byteInputStream()

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

    fun getHtmlGherkinManualList() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <h2>MANUAL Test1</h2>
        <ol>
            <li>First list item</li>
            <li>Second list item</li>
        </ol>
         <pre>
        <gherkin>
        Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun headlineBetweenGherkin() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <pre>
        <gherkin>
        <h2>MANUAL Test1</h2>
         Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun headlineBetweenPre() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <pre>
      <h2>MANUAL Test1</h2>
      <gherkin>
         Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlGherkinDescriptionText() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <p>This is a descriptive text.</p>
        <pre>
        <gherkin>
        Feature: Test Feature
             Scenario: Test Scenario 1
              Given a working Gherkin parser
              And some Gherkin text
              When I test the Gherkin parser
               Then I get a valid Document containing the expected information
        </gherkin>
        </pre>
        <p>This is another descriptive text.</p>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlGherkinSimple() =
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
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlGherkinMultipleScenario() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
      <pre>
        <gherkin>
         Feature: Test Feature
            Scenario: Test Scenario 1
                When I test the Gherkin parser

            Scenario: Test Scenario 2
                When I test the Gherkin parser again
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun getHtmlGherkinMultipleSteps() =
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
                  But the Document is not modified
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun getheaderOutsideOfPre() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <h2>This is a header</h2>
      <pre>
        <gherkin>
          Feature: Test Feature
            Scenario: Test Scenario 1
                  Given a working Gherkin parser
                  And some Gherkin text
                  When I test the Gherkin parser
                  Then I get a valid Document containing the expected information
                  But the Document is not modified
        </gherkin>
        </pre>
    </body>
    </html>
    """.byteInputStream()

    fun getPTags() =
        """
    <!DOCTYPE html>
    <html lang="en">
    <body>
    <h1>header1</h1>
    <p>This is a description 1</p>
      <pre>
        <gherkin>
          Feature: Test Feature
            Scenario: Test Scenario 1
                  Given a working Gherkin parser
                  And some Gherkin text
                  When I test the Gherkin parser
                  Then I get a valid Document containing the expected information
                  But the Document is not modified
        </gherkin>
      </pre>
      <p>This is a description 2</p>
    </body>
    </html>
    """.byteInputStream()
}
