/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import org.apache.poi.xssf.usermodel.XSSFCell;


public class CellWidthEstimator {

    private CellWidthEstimator() {
        
    }

	public static double getCellWidth(XSSFCell cell, String value) {
		return cell.getCellStyle().getWrapText() ? 2 : value.length() + 2;
	}
}
