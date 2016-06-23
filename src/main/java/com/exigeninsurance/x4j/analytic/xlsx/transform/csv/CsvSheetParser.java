/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class CsvSheetParser extends SheetParser {
	
	private final XSSFSheet sheet;
	
	public CsvSheetParser(XSSFSheet sheet,  ReportContext reportContext) {
		super( reportContext);
		this.sheet = sheet;
	}

	@Override
    public CellNode createCellNode(XSSFSheet xssfSheet, XSSFCell cell,
                                   int index, XLSXExpression expr, Node parent) {
		return  new CsvCellNode(sheet,cell,expr);
	}

	@Override
    public Node createRowNode(XSSFSheet xssfSheet, XSSFRow row) {
		return new CsvRowNode(sheet);
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
    public XLSXExpression createHeaderExpression(XLSXExpression expression) {
        return expression;
    }

    @Override
    protected Node createTailNode() {
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
}
