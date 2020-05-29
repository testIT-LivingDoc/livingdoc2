Feature: Calculator
  Scenario: The calculator can add
    Given a calculator
    When I add 2 and 3
    Then I get 5
    But result is less than 10
    And result is greater than 0