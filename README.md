nuxeo-directory-connector
=========================

Nuxeo Directory Connector is a simple Addon for Nuxeo Platforms that allows to wrap a external service as a Directory.

Basically, what this addons provides is all the plumbing code to expose a Java Class implementing a simple interface as a Nuxeo Directory.

Typical use case is to wrapp a remote WebService as a Nuxeo Directory.

Usaging a directory to wrap a WebService provides some direct benefits :

 - ability to use a XSD schema to define structure and Layout to define display (Nuxeo Studio friendly)

 - ability to reuse the FieldMapping and Caching features

 - entries are exposed as DocumentModels and can be rendered via Layouts


