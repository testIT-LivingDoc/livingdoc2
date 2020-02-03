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
            But the Document is not modified
    """.trimIndent().byteInputStream()

internal fun multipleStepScenarioInGermanGherkin() =
    """
        # language: de
        Funktionalität: Funktionstest
          Szenario: Test eines Szenarios
            Gegeben sei ein funktionierender Gherkin-Parser
            Und etwas Gherkin-Text
            Wenn ich den Parser teste
            Dann bekomme ich ein korrektes Dokument mit den erwarteten Informationen
            Aber das Dokument ist nicht modifiziert
    """.trimIndent().byteInputStream()

internal fun multipleStepScenarioInEmojiGherkin() =
    """
        # language: em
        📚: Funktionstest
          📕: Test eines Szenarios
            😐 ein funktionierender Gherkin-Parser
            😂 etwas Gherkin-Text
            🎬 ich den Parser teste
            🙏 bekomme ich ein korrektes Dokument mit den erwarteten Informationen
            😔 das Dokument ist nicht modifiziert
    """.trimIndent().byteInputStream()

internal fun scenarioOutlineGherkin() =
    """
        Feature: Test Feature
          Scenario Outline: eating
            Given there are <start> cucumbers
            When I eat <eat> cucumbers
            Then I should have <left> cucumbers
          
            Examples:
              | start | eat | left |
              |    12 |   5 |    7 |
              |    20 |   5 |   15 |
    """.trimIndent().byteInputStream()



