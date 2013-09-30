/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class CsvCellNode extends CellNode {

	private XSSFCell cell;

	public CsvCellNode(XSSFSheet sheet, XSSFCell cell, XLSXExpression expr) {
		super(sheet, cell, expr);
        setCell(cell);
	}

	@Override
	public void process(XLXContext context) throws Exception {
		Object value = expression.evaluate(context);
		if(value != null){
			context.write(format(context, value));
		}
	}

	public String format(XLXContext context,Object value) {
		if (value instanceof String) {
			return escape(value);
		}
		if (value instanceof Number) {
			return value.toString();
		}
		if (value instanceof Date) {
			return context.defaultDateFormat((Date)value);
		}
		if (value instanceof Money) {
			return ((Money) value).getValue().toString();
		}
		if (value instanceof Link) {
			return ((Link) value).getLabel();
		}

		return value.toString();
	}

	static String escape(Object value) {

		StringBuilder builder = new StringBuilder();
		builder.append('\"');
		for (char ch : value.toString().toCharArray()) {
			builder.append(ch);
			if (ch == '\"') {
				builder.append(ch);
			}	
		}
		builder.append('\"');
		return builder.toString();
	}

	public void setCell(XSSFCell cell) {
		this.cell = cell;
	}

	public XSSFCell getCell() {
		return cell;
	}
}
