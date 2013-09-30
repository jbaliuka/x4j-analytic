/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import org.apache.poi.POIXMLDocumentPart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConnections;


public class XLSXConnections extends POIXMLDocumentPart {
	
	private CTConnections connections;

	public XLSXConnections()  {
			
	}
	
	public CTConnections getCTConnections(){
		
		if(connections == null){
			connections = CTConnections.Factory.newInstance();
		}
		return connections;
		
	}

}
