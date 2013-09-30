/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


final class HTMLRowNode extends Node {

	private final XSSFRow row;

	public HTMLRowNode(XSSFSheet sheet, XSSFRow row) {
		super(sheet);
		this.row = row;

	}

	@Override
	public void process(XLXContext context) throws Exception {

		if (!getChildren().isEmpty()) {

			context.write("<tr>");			
			super.process(context);
			context.write("</tr>\n");
			context.nextRow();

		}
	}

	public XSSFRow getRow() {
		return row;
	}

}
