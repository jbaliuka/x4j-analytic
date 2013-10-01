x4j-analytic
============
x4j-analytic is an open source XLSX format template engine API for Java programming language. 
X4J is used as the embedded library in Java applications to implement full blown reporting solutions.

## Performance

X4J Engine is designed to produce relatively large reports and to consume reasonable amount of memory on reporting servers, WEB applications, batch reports.
This implementation uses sequential processing and normally X4J should be able to produce report with a million rows in a couple of seconds using constant memory.
Pivot Report feature helps to manage large amounts of data.

## Excel Support

X4J primary input/output format is XLSX, Excel is used as design to edit templates. Engine has limited capabilities to export report to other formats too: pdf, csv ,html, xml.
Engine is usually used to implement Pivot reports with Excel table as source data. X4J binds SQL query data to Excel tables and columns  by name without  special template language or tags.
Excel XML Map is also supported  to import/export Table data as XML.

## Template Language

Advanced reports might use optional template language and scripting. It  might be useful for simple  dynamic  headers or localized strings but
we suggest not to  abuse programming concepts for reports, [language](https://github.com/jbaliuka/x4j-analytic/wiki/Template-Language) 
is very simple but it is better extend engine itself to add features or to solve specific problems.

## Samples

[View](https://github.com/jbaliuka/x4j-analytic/blob/master/samples/src/test/java/x4j/samples/X4JEngineTest.java) usage examples online or clone git repository



Normally we use two files for every report, query and parameter declarations are stored as [XML](https://github.com/jbaliuka/x4j-analytic/wiki/Report-Definition-Schema)
Template is a regular Excel file in XLSX format with Table and Pivot Table and engine populates Table with query results. Excel Table name should match query name in XML file.
Context variables might referenced  as ${myContextVariable}. Templates also support Velocity like  language with loops and flow control statements: #if,#for, ..., #end

##  Building X4J from Source Code
[Maven](http://maven.apache.org/) is used to build X4J 
 * git clone https://github.com/jbaliuka/x4j-analytic.git
 * mvn install 








