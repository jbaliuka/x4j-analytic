/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public class OddFooterRule extends HeaderFooterApplicabilityRule {

	@Override
	public boolean isApplicable(int pageNumber, XSSFSheet sheet) {
		String firstHeader = sheet.getCTWorksheet().getHeaderFooter().getFirstFooter();
		String evenHeader = sheet.getCTWorksheet().getHeaderFooter().getEvenFooter();
		if (pageNumber == 1 && isHeaderFooterValid(firstHeader)) {
			return false;
		}
        return !((pageNumber % 2 == 0) && isHeaderFooterValid(evenHeader));
    }

}
