/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author astanzys
 *
 */


public class CurrencyTreeNode {
	
	private String currencyCd;
	private List <CurrencyFormat> formats;
	
	public CurrencyTreeNode(String currencyCd) {
		this.currencyCd = currencyCd;
		formats = new ArrayList<CurrencyFormat>();
	}
	
	public String getCurrencyCd() {
		return currencyCd;
	}
	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	public List<CurrencyFormat> getFormats() {
		return formats;
	}

	public void setFormats(List<CurrencyFormat> format) {
        formats = format;
	}
	
	public CurrencyFormat getCurrencyFormat(String countryCd) {
		for (CurrencyFormat format : formats) {
			if (format.getCountryCd().equals(countryCd)) {
				return format;
			}
		}
		return null;
	}
}
