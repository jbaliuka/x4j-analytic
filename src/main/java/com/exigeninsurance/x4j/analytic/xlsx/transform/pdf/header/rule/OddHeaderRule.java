/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public class OddHeaderRule extends HeaderFooterApplicabilityRule {

	@Override
	public boolean isApplicable(int pageNumber,XSSFSheet sheet) {
		String firstHeader = sheet.getCTWorksheet().getHeaderFooter().getFirstHeader();
		String evenHeader = sheet.getCTWorksheet().getHeaderFooter().getEvenHeader();
		if (pageNumber == 1 && isHeaderFooterValid(firstHeader)) {
			return false;
		}
        return !((pageNumber % 2 == 0) && isHeaderFooterValid(evenHeader));
    }

}
