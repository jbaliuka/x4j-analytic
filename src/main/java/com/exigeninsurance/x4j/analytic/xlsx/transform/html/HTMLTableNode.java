/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


final class HTMLTableNode extends TableNode {
	public HTMLTableNode(XSSFSheet sheet, Node parent, Table table) {
		super(sheet, table);
	}

	@Override
	public void process(XLXContext context) throws Exception {
		context.write("<tr><td>\n<table>\n");
		int tableId = (int) getTable().getCTTable().getId();
		context.setTableId(tableId);
		context.setTableStart(getTable().getStartCellReference().getRow());		
		context.getTableStylesMap().put(tableId, extractTableStyleName());
		
		super.process(context);
		
		context.setTableId(XLXContext.NOT_A_TABLE);
		context.write("\n</table>\n</td></tr>\n");
	}
}