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


public class CsvCurrencyFormatLine extends CsvLine {

	private String currencyCd;
	private String excelDataFormat;
	private String decimalDataPattern;
	
	public CsvCurrencyFormatLine (List<String> tokens) {
		currencyCd = tokens.get(0);
		countryCd = tokens.get(1);
		excelDataFormat = tokens.get(2);
		decimalDataPattern = tokens.get(3);
	}
	
	public String getCurrencyCd() {
		return currencyCd;
	}
	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}
	public String getCountryCd() {
		return countryCd;
	}
	public void setCountryCd(String countryCd) {
		this.countryCd = countryCd;
	}
	public String getExcelDataFormat() {
		return excelDataFormat;
	}
	public void setExcelDataFormat(String excelDataFormat) {
		this.excelDataFormat = excelDataFormat;
	}
	public String getDecimalDataPattern() {
		return decimalDataPattern;
	}
	public void setDecimalDataPattern(String decimalDataPattern) {
		this.decimalDataPattern = decimalDataPattern;
	}
	
	public Object valueOf() {
		return new CurrencyFormat(countryCd, excelDataFormat, decimalDataPattern);
	}
	
	
}
