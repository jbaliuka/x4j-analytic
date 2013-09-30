/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public final class NoOpExpression implements XLSXExpression {
	private final XSSFCell cell;

	public NoOpExpression(XSSFCell cell) {
		this.cell = cell;
	}

	public Object evaluate(XLXContext context)
	throws Exception {						
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK :
			return "";
		case Cell.CELL_TYPE_NUMERIC :
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING :
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_FORMULA :
			return cell.getCTCell().getV();
		case Cell.CELL_TYPE_BOOLEAN :
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_ERROR :
			return cell.getErrorCellValue();
		default:
			return null;
		}
	}

	
}