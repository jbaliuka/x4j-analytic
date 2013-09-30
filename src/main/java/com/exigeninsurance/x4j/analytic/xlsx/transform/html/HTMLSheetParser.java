/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TextNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;


class HTMLSheetParser extends SheetParser {

	private final XSSFSheet sheet;

	public HTMLSheetParser(XSSFSheet sheet, XLSXStylesTable styles,ReportContext reportContext) {
		super(styles, reportContext);
		this.sheet = sheet;
	}

	@Override
    public Node createRowNode(XSSFSheet sheet, Node parent, XSSFRow row) {

		return new HTMLRowNode(sheet, row);
	}

	@Override
    public CellNode createCellNode(XSSFSheet sheet, XSSFCell cell, int index,
                                   XLSXExpression expr, Node parent) {

		return  new HTMLCellNode(sheet,cell, expr);
	}

	@Override
	public TableNode createTableNode(XSSFSheet sheet, Node top, Table table) {

		return new HTMLTableNode(sheet, top, table);
	}

	@Override
	public Node createEmptyRow(XSSFSheet sheet,Node parent, int row) {

		return new Node(sheet){
			@Override
			public void process(XLXContext context) throws Exception {
				context.write("<tr><td>&nbsp;</td></tr>");
				context.nextRow();
			}
		};
	}

	@Override
	public Node createEmtyCell(XSSFSheet sheet, XSSFCell cell, Node parent) {

		return new TextNode(sheet,"<td>&nbsp;</td>");
	}

	protected String head(String str) {
		

		return "\n<table id=\""+ sheet.getSheetName() +"\">\n";

	}
	
	@Override
	protected Node createPictureNode(XSSFSheet sheet, Picture picture) {
		return new Node(sheet){
			@Override
			public void process(XLXContext context) throws Exception {

			}
		};
	}

	@Override
	public Node createTotalsNode(XSSFSheet xssfSheet, TableNode tableNode,
			XSSFRow row) {
		return createRowNode(xssfSheet, tableNode, row);
	}

    @Override
    public XLSXExpression createHeaderExpression(final XLSXExpression expression) {
        return new XLSXExpression() {
            @Override
            public Object evaluate(XLXContext context) throws Exception {
                return WrappingUtil.htmlWrap(WrappingUtil.wrap((String) expression.evaluate(context)));
            }

            
        };
    }

    @Override
    protected Node createTailNode() {
        return new TextNode(getSheet(), "\n</table>");
    }

}
