/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Attribute;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;


public class AlignmentFinder {

	private boolean autoAlignDates;
	private boolean autoAlignCurrency;

	public AlignmentFinder(ReportContext context) {
		Attribute currency = context.getMetadata().getAttributeByName("currency-alignment");
		Attribute dates = context.getMetadata().getAttributeByName("date-alignment");
		autoAlignDates = isEnabled(dates);
		autoAlignCurrency = isEnabled(currency);
	}

	public short excelAlignment(Object value, XSSFCellStyle style) {
		return determineAlignment(value, style).getExcel();
	}

	public Alignment determineAlignment(Object value, XSSFCellStyle style) {
		if (value instanceof Money && autoAlignCurrency) {
			return Alignment.RIGHT;
		}
		else if (value instanceof Date && autoAlignDates) {
			return Alignment.CENTER;
		}

		int cellAlignment = style.getAlignment();
		switch (cellAlignment) {
			case(CellStyle.ALIGN_CENTER):
				return Alignment.CENTER;
			case(CellStyle.ALIGN_RIGHT):
				return Alignment.RIGHT;
			default:
				return Alignment.LEFT;
		}
	}

	private boolean isEnabled(Attribute a) {
		return a == null || a.getValue().toLowerCase().equals("enabled");
	}
}
