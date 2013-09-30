/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;


public class XLSXTheme extends  POIXMLDocumentPart{
	
	private ThemeDocument doc;
	
	public XLSXTheme(PackagePart part, PackageRelationship rel) throws IOException, XmlException {
		super(part, rel);
		readFrom(part.getInputStream());
	}

	private void readFrom(InputStream inputStream) throws XmlException, IOException {		
		doc = ThemeDocument.Factory.parse(inputStream);		
	}

	public CTOfficeStyleSheet getTheme(){
		return doc.getTheme();
	}
	
}
