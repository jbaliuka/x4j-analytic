/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import java.util.List;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


final class XMLTableNode extends TableNode {
	public XMLTableNode(XSSFSheet sheet, Node parent, Table table) {
		super(sheet, table);
	}


	@Override
	public void process(XLXContext context) throws Exception {

		CTTableColumns cols = getTable().getCTTable().getTableColumns();
		int index = 0;
		List<Node> cells = getForEach().getChildren().get(0).getChildren();
		int offset = getTable().getStartCellReference().getCol();
		
		for(CTTableColumn  col : cols.getTableColumnArray()){
			String element = SST.toXMLEment( col.getName());
			((XMLCellNode) cells.get(index + offset)).setElement(element);
			index++;
		}
		
		context.write("<root>\n");
		getForEach().process(context);
		context.write("</root>");

	}

}