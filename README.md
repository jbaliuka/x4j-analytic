x4j-analytic
============
x4j-analytic is an open source XLSX format template engine API for Java programming language. 
X4J is used as the embedded library in Java applications to implement full blown reporting solutions.


X4J Engine is designed to produce relatively large reports and to consume reasonable amount of memory on reporting servers, WEB applications, batch reports.
This implementation uses sequential processing and normally X4J should be able to produce report with a million rows in a couple of seconds using constant memory.
 
X4J uses POI to read templates but it does not use any  abstraction layers to produce output, we write raw byte stream as the transformation result.

XLSX  format is limited to 1,048,576 rows per sheet and Excel manages to open  large reports on modern machines. It is manageable with  Pivot reports feature.


X4J primary input/output format is XLSX and Excel should be used to edit templates. Engine has limited capabilities to export report to other formats too: pdf, csv ,html, xml.

X4J is usually used to implement Pivot reports with Excel table as source data. X4J binds SQL query data to Excel tables and columns  by name without  special template language or tags.
Reports might use template language, scripting and placeholders but it is the optional feature. Template expressions  might be useful for simple  dynamic  headers,  localized strings but
we suggest not to  abuse programming concepts for reports,  extend engine itself to add features or to solve specific problems.

View usage examples online or clone git repository

https://github.com/jbaliuka/x4j-analytic/blob/master/samples/src/test/java/x4j/samples/X4JEngineTest.java

Normally we use two files for every report, queries and parameters declarations are stored as XML https://github.com/jbaliuka/x4j-analytic/wiki/Report-Definition-Schema
Template is a regular Excel file in XLSX format with Table and Pivot Table and engine populates Table with query results. Excel Table name should match query name in XML file.
Context variables might referenced  as ${myContextVariable}. Templates also support Velocity like  language with loops and flow control statements: #if,#for, ..., #end

##  Building X4J from Source 
Maven is used to build X4J from source code http://maven.apache.org/
 * clone git repo: https://github.com/jbaliuka/x4j-analytic.git
 * build from source: mvn install 








