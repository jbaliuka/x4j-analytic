/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public abstract class HeaderFooterApplicabilityRule {
	
	public abstract boolean isApplicable(int pageNumber, XSSFSheet sheet);
	
	public boolean isHeaderFooterValid(String contents) {
		return contents != null;

	}
}
