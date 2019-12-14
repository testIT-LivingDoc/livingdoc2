# Calculator

Examples

|  a |  b | a + b = ? | a - b = ? | a * b = ? | a / b = ? |
|----|----|-----------|-----------|-----------|-----------|
|  0 |  0 |  0        |  0        |  0        | NaN       |
|  1 |  0 |  1        |  1        |  0        | Infinity  |
|  0 |  1 |  1        | -1        |  0        |  0        |
|  1 |  1 |  2        |  0        |  1        |  1        |
| -1 |  0 | -1        | -1        | -0        | -Infinity |
|  0 | -1 | -1        |  1        | -0        | -0        |
| -1 |  1 |  0        | -2        | -1        | -1        |
|  1 | -1 |  0        |  2        | -1        | -1        |
| -1 | -1 | -2        |  0        |  1        |  1        |

# Scenario for testing bug with variables length

Failed finding matching template before

- adding 10 and 10 and 10 and 10 and 10 and 10 and 10 equals 70
- adding 10 and 10 and 10 and 10 and 10 and 10 and 100000000000000000000000 equals 100000000000000000000060
- adding 120 and 120 and 0 and 0 and 0 and 0 and 0 equals 240

- adding 01111 and 02222 equals 03333
- adding 11111 and 22222 equals 33333

- adding 12345678 and 98765432 equals 111111110

Succeeded finding matching template before

- adding 1111 and 2222 equals 3333
- adding 1111 and 2222 equals 03333
- adding 1111 and 02222 equals 03333

# Scenario 1

- adding 1 and 2 equals 3
- adding 1 and 4 equals 5
- multiplying 3 and 2 equals 6

# Scenario 2

- add 9 to itself and you get 18

# Scenario 3

- add 5 to itself and you get 10
