/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.util.Map;

/**
 * @author astanzys
 *
 */

public class PdfStylesTable {
	private Map<String,TableStyle> tableStyles;
	private String defaultTableStyle;
	
	public Map<String, TableStyle> getTableStyles() {
		return tableStyles;
	}

	public void setTableStyles(Map<String, TableStyle> tableStyles) {
		this.tableStyles = tableStyles;
	}

	public String getDefaultTableStyle() {
		return defaultTableStyle;
	}
	
	public void setDefaultTableStyle(String defaultTableStyle) {
		this.defaultTableStyle = defaultTableStyle;
	}
}
