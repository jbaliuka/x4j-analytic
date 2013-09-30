/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.localization;


/**
 * 
 * @author lsucila
 *
 */

public enum FormatType {
	CURRENCY("currency-formats", "/currencyFormats.csv"), 
	DATE("date-formats", "/dateFormats.csv"),
	DATE_TIME("date-time-formats", "/dateTimeFormats.csv");
	
	private String attribute;
	private String metadataFile;
	
	FormatType(String attributeName, String metadataFile) {
        attribute = attributeName;
		this.metadataFile = metadataFile;
	}
	
	public String getFormatAttributeName() {
		return attribute;
	}
	
	public String getMissingMetadataFallbackFile() {
		return metadataFile;
	}
}
