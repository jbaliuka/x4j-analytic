x4j-analytic
============
x4j-analytic is an open source XLSX format template engine API for Java programming language. 
X4J can be used as the embedded library in Java applications to implement full blown reporting solutions.


X4J Engine is designed to produce relatively large reports and to consume reasonable amount of memory on reporting servers, WEB applications, batch reports.
Implementation uses sequential processing and it might compromise some of features if it requires random access.
Normally X4J should be able to produce report with a million rows in a couple of seconds and it should use constant memory.
 
X4J uses POI to read templates but it does not use any XML libraries or  format abstraction layers to produce output, we write raw byte stream as the transformation result.
XLSX  format is limited to 1,048,576 rows per sheet but multiple sheets might be used to produce larger reports, 
Excel manages to open  large reports on modern machines and large amounts of data are manageable  in  Pivot reports.


Primary input/output format is XLSX and Excel should be used to edit templates. Engine has limited capabilities to export report to other formats too: pdf, csv ,html, xml.
X4J is usually used to implement Pivot reports with Excel table as source data. X4J binds SQL query data to Excel tables and columns  by name without  special template language or tags.
Reports might use template language, scripting and placeholders but it is the optional feature. Template expressions  might be useful for simple  dynamic  headers,  localized strings but
we suggest not to  abuse programming concepts for reports,  extend engine to add features or to solve specific problems.


API might change in the future but backward compatibility will be preserved. It is planned to add more extension points instead of features.
Source repository contains sample templates and code to initialize  report context, please clone git repo and see sample directory to get started, read API documentation.
Veiw samples online https://github.com/jbaliuka/x4j-analytic/blob/master/samples/src/test/java/x4j/samples/X4JEngineTest.java

Maven is used to build X4J from source code http://maven.apache.org/



