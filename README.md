x4j-analytic [![Build Status](https://travis-ci.org/jbaliuka/x4j-analytic.svg?branch=master)](https://travis-ci.org/jbaliuka/x4j-analytic)
============
x4j-analytic is an open source XLSX format template engine API for Java programming language. 
X4J is used as the embedded library in Java applications to implement full blown reporting solutions.

## License

This projected is licensed under the terms of the [Apache v2 license](http://www.apache.org/licenses/LICENSE-2.0.html)

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

[View](https://github.com/jbaliuka/x4j-analytic/blob/master/samples/src/test/java/x4j/samples/X4JEngineTest.java) code examples online or clone git repository.
[View](https://github.com/jbaliuka/x4j-analytic/tree/master/samples/src/test/resources/samples) sample reports on repository.


Normally we use two files for every report, query and parameter declarations are stored as [XML](https://github.com/jbaliuka/x4j-analytic/wiki/Report-Definition-Schema)
Template is a regular Excel file in XLSX format with Table and Pivot Table and engine populates Table with query results. Excel Table name should match query name in XML file.
Context variables might referenced  as ${myContextVariable}. Templates also support Velocity like  language with loops and flow control statements: #if,#for, ..., #end

##  Building X4J from Source Code
[Maven](http://maven.apache.org/) is used to build X4J 
 * git clone https://github.com/jbaliuka/x4j-analytic.git
 * mvn install 


## Similar Projects

There are other similar Excel template engines and they should work for small Excel reports too.

 * [JETT](http://jett.sourceforge.net/index.html)  (Java Excel Template Translator) is a Java 5.0 API that allows speedy creation of Excel spreadsheet reports using Excel spreadsheet templates.
 * [JXLS](http://jxls.sourceforge.net)  is a small and easy-to-use Java library for writing Excel files using XLS templates and reading data from Excel into Java objects using XML configuration.
 

X4J is designed for huge reports. Automatic table binding is also a nice X4J Engine feature, 
it helps to make maintainable Excel templates because we can avoid tags and scripting for simple tables. 
Pivot can replace most of sophisticated grouping and summarization scripts or tags. 

 
We believe Excel report should be a simple list with optional pivot because Excel is designed to be an interactive tool,  
let user to customize pivots, conditional formating, apply what if analysis.
Fancy formatting obfuscates data and it is not practical. There are better formats for small fancy formated documents: PDF with XFA or Word docx with XML binding.

 * [docx4j](http://www.docx4java.org/trac/docx4j)  is an excellent tool for Word documents and XML binding.
 * [pdfbox](http://pdfbox.apache.org/) is an excellent tool to work with PDF format and it supports XFA forms.
        

   





