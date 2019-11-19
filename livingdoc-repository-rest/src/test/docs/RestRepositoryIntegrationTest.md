# LivingDoc

This is the integration test for the REST-API.

Host has to have a protocol defined (http:// or https://).
Path has to start with /.
File-Path is the path to the file that WireMock serves.
If you do not want to serve a file, pick a path that does not exist.

Examples

| Host | Port | Path | File-Path | Throws RestDocumentNotFoundException |
|------|------|------|------|------------------------------------|
| http://thislivingdocdoesnotexist.com | 0 | /NoFile.html | | True |
| http://localhost | 8000 | /Testing.html | Testing.html | False |
