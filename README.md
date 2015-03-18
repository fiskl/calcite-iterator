# Calcite Document Iterator
Implementing a calcite adapter for tables backed by a common iterator.

Motivation
----------
Calcite seems to be suited to ad-hoc flattening of nested data structures in a series of documents. However, representing every column as a struct seems to be a clumsy way of achieving it, and potentially leads to complicated SQL queries. Simply making a synthetic table from each nested structure also seems clumsy.

A apparently simple way to achieve this aim is to back the Calcite tables with a document iterator. Tables would be created for each cardinality change in the document. The iterator would populate the in-memory tables with values from a single document, allow Calcite to join and flatten the document, then re-populate with the next document's data.

Plea for help
-------------
This project was created to ask for help implementing the feature in the appropriate way.

The current code demonstrates the concept, and implements a document iterator. Running the sole test injects some placeholder data and runs a SQL query. However, the project uses the debugging hook to insert a document iterator RelNode in the appropriate place. Any clues/contributions that point it in the right direction would be greatly appreciated!

Thanks for your help...
