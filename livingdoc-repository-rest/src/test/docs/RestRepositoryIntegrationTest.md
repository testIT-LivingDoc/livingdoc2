# LivingDoc

This is the integration test for the REST-API.

Host has to have a protocol defined (http:// or https://).
Path has to start with /.
File-Path is the path to the file that WireMock serves.
If you do not want to serve a file, pick a path that does not exist.

## Examples

### DecisionTable to check for thrown exception

| Host | Port | Path | File-Path | Throws RestDocumentNotFoundException |
|------|------|------|------|------------------------------------|
| http://thislivingdocdoesnotexist.com | 0 | /NoFile.html | | True |
| http://localhost | 8000 | /Testing.html | Testing.html | False |

### Scenarios to check for content of received html

The file used is Testing.html.

- Example 0 should exist
- Example 3 should not exist

- Example 0 should be a DecisionTable

- Example 0 should have 2 rows

- Row 0 of example 0 should have 6 headers
- Row 1 of example 0 should have 6 headers

This cannot be tested yet because LivingDoc cannot read long strings as input
Content of row 0 of example 0 should be 1, 1, 2, 0, 1, 1
Content of row 1 of example 0 should be 1, 0, 1, 1, 0, Infinity

This cannot be tested yet because LivingDoc cannot read long strings as input
Header belonging to row 1 of example 0 should be a, b, a + b = ?, a - b = ?, a * b = ?, a / b = ?
