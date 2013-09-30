/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;

import java.util.List;

/**
 * 
 * @author astanzys
 *
 */


public class CsvDateFormatLine extends CsvLine {
	
	private String excelDateFormat;
	private String dateFormat;
	
	public CsvDateFormatLine(List<String> tokens) {
		countryCd = tokens.get(0);
		excelDateFormat = tokens.get(1);
		dateFormat = tokens.get(2);
	}
	
	public String getExcelDateFormat() {
		return excelDateFormat;
	}
	public void setExcelDateFormat(String excelDateFormat) {
		this.excelDateFormat = excelDateFormat;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Object valueOf() {
		return new DatesFormat(excelDateFormat, dateFormat);
	}
	
	
}
