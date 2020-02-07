Feature: Calculator
  Scenario: The calculator can process data tables
    Given a calculator
    When I perform:
      | 2 | 3 | 5 |
      | 1 | 1 | 2 |

  Scenario: The calculator can process doc strings
    Given a calculator
    When I read
      """
      A long long text without any numbers
      """
    Then I do nothing
