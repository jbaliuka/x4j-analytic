/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class PdfTableNode extends TableNode {

	public PdfTableNode(XSSFSheet sheet, Table table) {
		super(sheet, table);
	}

	@Override
	public void process(XLXContext context) throws Exception {
		PdfContext pdfContext = (PdfContext) context;
		int tableId = (int) getTable().getCTTable().getId();
		pdfContext.setTableId(tableId);
		context.getTableStylesMap().put(tableId, extractTableStyleName());
		super.process(context);
		context.setTableId(XLXContext.NOT_A_TABLE);
	}
}
