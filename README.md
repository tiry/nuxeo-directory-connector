nuxeo-directory-connector
=========================

## What is this project ?

Nuxeo Directory Connector is a simple Addon for Nuxeo Platforms that allows to wrap a external service as a Directory.

Basically, what this addons provides is all the plumbing code to expose a Java Class implementing a simple interface as a Nuxeo Directory.

## Why would you use this ?

You should consider this as a sample code that you can use as a guide to implement a new type of Directory on top of a custom service provider.

Typical use case is to wrapp a remote WebService as a Nuxeo Directory.

Usaing a directory to wrap a WebService provides some direct benefits :

 - ability to use a XSD schema to define the structure of the entities your expose 

      - entries are exposed as DocumentModels
      - you can then use Nuxeo Layout system to define display 
      - you can then use Nuxeo Studio to do this configuration

 - ability to reuse existing Directory features

      - Field Mapping
      - Entries caching
      - Widgets to search / select an entry

## History

This code was initially written against a Nuxeo 5.4 to be able to resuse a custom WebService as user provider.


