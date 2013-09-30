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


public class CurrencyFormat {
	
	private String countryCd;
	private String excelFormat;
	private String decimalFormat;
	public CurrencyFormat(String countryCd, String excelFormat,
			String decimalFormat) {
		super();
		this.countryCd = countryCd;
		this.excelFormat = excelFormat;
		this.decimalFormat = decimalFormat;
	}
	
	public String getCountryCd() {
		return countryCd;
	}
	public void setCountryCd(String countryCd) {
		this.countryCd = countryCd;
	}
	public String getExcelFormat() {
		return excelFormat;
	}
	public void setExcelFormat(String excelFormat) {
		this.excelFormat = excelFormat;
	}
	public String getDecimalFormat() {
		return decimalFormat;
	}
	public void setDecimalFormat(String decimalFormat) {
		this.decimalFormat = decimalFormat;
	}
	
	
}
