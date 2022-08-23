# database

A relational database server built with Java. The server receives incoming requests (conforming to a standard query language) and then interrogates and manipulates a set of stored records. The server maintains persistent data as a collection of files on filesystem.

Supports the following main types of query:

* USE: changes the database against which the following queries will be run  
* CREATE: constructs a new database or table (depending on the provided parameters)  
* INSERT: adds a new record (row) to an existing table  
* SELECT: searches for records that match the given condition  
* UPDATE: changes the existing data contained within a table  
* ALTER: changes the structure (columns) of an existing table  
* DELETE: removes records that match the given condition from an existing table  
* DROP: removes a specified table from a database, or removes the entire database  
* JOIN: performs an inner join on two tables (returning all permutations of all matching records)  
