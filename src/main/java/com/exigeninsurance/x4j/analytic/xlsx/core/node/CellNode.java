
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.utils.ColorHelper;


abstract public class CellNode extends Node {
	
	public static final String DEFAULT_FILL = "#FFFFFF";

	protected XLSXExpression expression;
	protected XSSFCell cell;
	private String axis;
	

	public CellNode(XSSFSheet sheet,XSSFCell cell,XLSXExpression expr) {
		super(sheet);
        expression = expr;
		this.cell = cell;
	}



	public CellNode(XSSFSheet sheet) {
		super(sheet);
	}


	public XLSXExpression getExpression() {
		return expression;
	}

    public void setExpression(XLSXExpression expression) {
        this.expression = expression;
    }

    public XSSFCell getCell() {

		return cell;
	}

	public void setAxis(String axis) {
		this.axis = axis;
	}

	public String getAxis() {
		return axis;
	}
	
	public String getFill(PdfStyle pdfStyle) {
		if (pdfStyle != null) {
			return ColorHelper.colorToHex(pdfStyle.getFill().getBgColor());
		}
		return DEFAULT_FILL;
	}

    public XSSFCellStyle getCellStyle() {
        return getCell().getCellStyle();
    }
}
