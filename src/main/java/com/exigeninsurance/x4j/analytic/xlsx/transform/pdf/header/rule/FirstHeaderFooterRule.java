/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public class FirstHeaderFooterRule extends HeaderFooterApplicabilityRule {

	public boolean isApplicable(int pageNumber, XSSFSheet sheet) {
		return pageNumber == 1;
	}

}
