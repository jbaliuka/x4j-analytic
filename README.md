x4j-analytic
============
x4j-analytic is an open source XLSX format template engine API for Java programming language. 
There are many other reporting tools but performance is an issue in our experience  with other tools and this library was initially developed for performance reasons only.
X4J should be used as the embedded library in Java applications to implement full blown reporting solutions.
Engine is designed to produce relatively large reports and to consume reasonable amount of memory on reporting servers or WEB applications, 
implementation uses sequential processing and it might compromise some of features if it requires random access,
actually we scan data twice to calculate colum width, but this feature might be removed for maximum performance. 
Normally X4J should be able to produce report with a million rows in a couple of seconds. 
X4J uses POI to read templates but it does not use any XML libraries or  format abstraction layers to produce output, we write raw byte stream as the transformation result.
XLSX  format is limited to 1,048,576 rows per sheet but multiple sheets might be used to produce larger reports, 
Excel manages to open very large reports on modern machines and large amounts of data are manageable  in  Pivot reports.


Primary input and output format is XLSX. Excel should be used to edit templates but engine has limited capabilities to export report to other formats too: pdf, csv ,html, xml.
X4J is usually used to implement Pivot reports with Excel table as source data. X4J binds Excel tables and columns  to SQL query by name without  special template language or tags.
Reports might use template language, scripting and placeholders but it is the optional feature and it might be useful for simple  dynamic expressions, headers,  localized strings.
We suggest to avoid abusing programming concepts for reports, programming is a solution for X4J engine itself, extend engine to add features or to solve specific problems.
X4J has a long history in industry but it used to be the part of reporting solution instead of API and API is designed just before to open source this code, 
API might change in the future but backward compatibility will be preserved. It is planned to add more extension points instead of features.
Source repository contains sample templates and code to initialize  report context, please clone git repo and see sample directory to get started, read API documentation.



