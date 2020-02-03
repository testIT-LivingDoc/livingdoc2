Feature: Calculator
  Scenario: The calculator can add
    Given a calculator
    When I add <summand1> and <summand2>
    Then I get <sum>
    But result is less than <upperbound>
    And result is greater than <lowerbound>

    Examples:
      | summand1 | summand2 | sum | upperbound | lowerbound |
      |        1 |        2 |   3 |          4 |          0 |
      |      123 |      123 | 246 |        256 |          0 |
      |        0 |        0 |   0 |          1 |         -1 |