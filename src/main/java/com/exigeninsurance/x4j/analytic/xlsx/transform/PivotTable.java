/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.PivotTableDefinitionDocument;


public class PivotTable extends POIXMLDocumentPart {

	private CTPivotTableDefinition ctPivotTable;

	public PivotTable() {
		setCtPivotTable(CTPivotTableDefinition.Factory.newInstance());
	}

	public PivotTable(PackagePart part, PackageRelationship rel)
			throws IOException {
		super(part, rel);
		readFrom(part.getInputStream());
	}

	public void setCtPivotTable(CTPivotTableDefinition ctPivotTable) {
		this.ctPivotTable = ctPivotTable;
		
	}

	public CTPivotTableDefinition getCtPivotTable() {
		return ctPivotTable;
	}

	public void readFrom(InputStream is) throws IOException {
		try {
			PivotTableDefinitionDocument doc = PivotTableDefinitionDocument.Factory.parse(is);
			setCtPivotTable(doc.getPivotTableDefinition());
		} catch (XmlException e) {
			throw new IOException(e);
		}
	}

	public XSSFSheet getXSSFSheet(){
		return (XSSFSheet) getParent();
	}

	public void writeTo(OutputStream out) throws IOException {
		PivotTableDefinitionDocument doc = PivotTableDefinitionDocument.Factory.newInstance();
		doc.setPivotTableDefinition(ctPivotTable);
		doc.save(out, DEFAULT_XML_OPTIONS);
	}

	@Override
	protected void commit() throws IOException {
		PackagePart part = getPackagePart();
		OutputStream out = part.getOutputStream();
		try{
			writeTo(out);
		}finally{
			out.close();
		}
	}
}
