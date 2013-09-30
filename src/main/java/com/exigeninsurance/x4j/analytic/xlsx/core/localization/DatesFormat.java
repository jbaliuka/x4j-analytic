/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;

/**
 * 
 * @author astanzys
 *
 */


public class DatesFormat {
	
	private String excelDateFormat;
	private String dateFormat;
	
	public DatesFormat(String excelDateFormat, String dateFormat) {
		this.excelDateFormat = excelDateFormat;
		this.dateFormat = dateFormat;
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
	
	
}
