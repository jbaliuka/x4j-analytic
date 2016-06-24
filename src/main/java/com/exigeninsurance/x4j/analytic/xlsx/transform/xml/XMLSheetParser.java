/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.EmptyNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


 class XMLSheetParser extends SheetParser {

	private final XSSFSheet sheet;

	public XMLSheetParser(XSSFSheet sheet, ReportContext reportContext) {
		super(reportContext);
		this.sheet = sheet;
	}

	@Override
    public Node createRowNode(XSSFSheet sheet, XSSFRow row) {

		return new XMLRowNode(sheet);
	}

	@Override
    public CellNode createCellNode(XSSFSheet sheet, XSSFCell cell, int index,
                                   XLSXExpression expr, Node parent) {

		return  new XMLCellNode(sheet,cell,expr);
			
	}

     @Override
     public XLSXExpression createHeaderExpression(XLSXExpression expression) {
         return expression;
     }

    @Override
    protected Node createTailNode() {
        return new Node(getSheet());
    }

    @Override
	public TableNode createTableNode(XSSFSheet sheet, Node top, Table table) {
		return new XMLTableNode(sheet, table);
	}

	@Override
	public Node createEmptyRow(XSSFSheet sheet, int row) {

		return new Node(sheet){
			@Override
			public void process(XLXContext context) throws Exception {			
				context.nextRow();
			}
		};
	}

	@Override
	public Node createEmtyCell(XSSFSheet sheet, XSSFCell cell) {

		return new EmptyNode(sheet);
	}

	protected String tail() {

		return  "";

	}


	protected String head() {
		

		return "";

	}

	@Override
	protected Node createPictureNode(XSSFSheet sheet, Picture picture) {
		
		return new Node(sheet);
	}

	@Override
	public Node createTotalsNode(XSSFSheet xssfSheet, TableNode tableNode,
			XSSFRow row) {
		
		return new EmptyNode(sheet);
	}
	
	

}
