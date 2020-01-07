package org.livingdoc.repositories.format

internal fun simpleGherkin() =
    """
        Feature: Test Feature
          Scenario: Test Scenario
            When I test the Gherkin parser
    """.trimIndent().byteInputStream()

internal fun multipleScenarioGherkin() =
    """
        Feature: Test Feature
          Scenario: Test Scenario 1
            When I test the Gherkin parser

          Scenario: Test Scenario 2
            When I test the Gherkin parser again
    """.trimIndent().byteInputStream()

internal fun multipleStepScenarioGherkin() =
    """
        Feature: Test Feature
          Scenario: Test Scenario 1
            Given a working Gherkin parser
            And some Gherkin text
            When I test the Gherkin parser
            Then I get a valid Document containing the expected information
    """.trimIndent().byteInputStream()
