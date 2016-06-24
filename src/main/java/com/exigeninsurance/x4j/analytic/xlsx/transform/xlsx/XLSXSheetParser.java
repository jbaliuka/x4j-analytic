/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.MergedCellsNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TextNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;


public class XLSXSheetParser extends SheetParser {

	private static final String SHEET_DATA = "</sheetData>";


	public XLSXSheetParser( ReportContext reportContext) {
		super(reportContext);
	}

	public XLSXSheetParser() {
		super(null);
	}

	protected String sheetData() {
		return SHEET_DATA;
	}

    @Override
    protected Node createTailNode() {
        Node tailNode = new Node(getSheet());
        tailNode.getChildren().add(new TextNode(getSheet(), sheetData()));
        tailNode.getChildren().add(new MergedCellsNode(getSheet()));
        tailNode.getChildren().add(new XLSXSheetTail(getSheet()));
        tailNode.getChildren().add(new TextNode(getSheet(), "</worksheet>"));
        return tailNode;
    }

    @Override
    public Node createRowNode(XSSFSheet xssfSheet, XSSFRow row) {
		return new XLSXRowNode(xssfSheet, row);
	}


	@Override
    public CellNode createCellNode(XSSFSheet sheet, XSSFCell cell, int index,
                                   XLSXExpression expr, Node parent) {

		XLSXCellNode node = new XLSXCellNode(sheet, cell, index, expr);
		node.setTableNode(isTableRow(cell.getRowIndex()));
		return node;
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
		return createRowNode(xssfSheet, row);
	}

    @Override
    public XLSXExpression createHeaderExpression(final XLSXExpression expression) {
        return new XLSXExpression() {
            @Override
            public Object evaluate(XLXContext context) throws Exception {
                Object value = expression.evaluate(context);
                if(value == null){ 
                	return "${" + expression.toString()  + "}";
                }
				return WrappingUtil.excelWrap(WrappingUtil.wrap(value.toString()));
            }

            
        };
    }
}
