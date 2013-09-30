/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;


public enum Alignment {
    LEFT(XSSFCellStyle.ALIGN_LEFT),
	CENTER(XSSFCellStyle.ALIGN_CENTER),
	RIGHT(XSSFCellStyle.ALIGN_RIGHT);

	private final short excel;

	private Alignment(short excel) {
		this.excel = excel;
	}

	public short getExcel() {
		return excel;
	}
}
