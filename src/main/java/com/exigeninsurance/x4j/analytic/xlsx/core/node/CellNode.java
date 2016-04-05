
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.utils.ColorHelper;


abstract public class CellNode extends Node {
	
	public static final String DEFAULT_FILL = "#FFFFFF";

	protected XLSXExpression expression;
	protected XSSFCell cell;
	private String axis;
	private ColorHelper colorHelper;
	

	public CellNode(XSSFSheet sheet,XSSFCell cell,XLSXExpression expr) {
		super(sheet);
        expression = expr;
		this.cell = cell;
		StylesTable stylesSource = sheet.getWorkbook().getStylesSource();
		CTOfficeStyleSheet office = null;
		if(stylesSource instanceof XLSXStylesTable){
			office = ((XLSXStylesTable)stylesSource).getStyleSheet();
		}
		this.setColorHelper(new ColorHelper(office)); 
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
			return getColorHelper().colorToHex(pdfStyle.getFill().getBgColor());
		}
		return DEFAULT_FILL;
	}

    public XSSFCellStyle getCellStyle() {
        return getCell().getCellStyle();
    }



	public ColorHelper getColorHelper() {
		return colorHelper; 
	}



	public void setColorHelper(ColorHelper colorHelper) {
		this.colorHelper = colorHelper;
	}
}
