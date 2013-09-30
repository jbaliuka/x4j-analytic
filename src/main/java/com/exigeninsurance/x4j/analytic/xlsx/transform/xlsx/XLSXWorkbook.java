/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


final  public class XLSXWorkbook extends XSSFWorkbook{
	
	
	public XLSXWorkbook(ZipPackage pack) throws IOException {
		super(pack);
	}

	public XLSXWorkbook(File template) throws IOException {
		super(template.getAbsolutePath());
	}

	@Override
	protected void read(POIXMLFactory factory,
			Map<PackagePart, POIXMLDocumentPart> context)
	throws OpenXML4JException {

		super.read(new XLSXFactory(), context);

	}
	   
	 
	protected void onDocumentRead() throws IOException {
		super.onDocumentRead();
		for(POIXMLDocumentPart p : getRelations()){
			if(p instanceof XLSXTheme) {

				((XLSXStylesTable)getStylesSource()).setTheme((XLSXTheme)p);

			}                

		}
		
		

	}

}
