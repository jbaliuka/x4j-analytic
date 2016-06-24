/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.AggregateExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.TableExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.TotalsExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.ForEachNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;


public final class TableParser {

	private static final String INTERNAL_ERROR = "internal error";
	/**
	 * 
	 */
	private final SheetParser sheetParser;
	private final XSSFRow row;
	private final Node top;
	private final XSSFSheet sheet;

	public TableParser(SheetParser sheetParser, XSSFRow row, Node top) {
		this.sheetParser = sheetParser;
        sheet = sheetParser.getSheet();
		this.row = row;
		this.top = top;
	}

	public void parse(Table table) {

		final String tableName = table.getCTTable().getName();

		int end = table.getEndCellReference().getRow();
		TableNode tableNode = sheetParser.createTableNode(sheet,top,table);
		top.getChildren().add(tableNode);
		int headerRowCount = (int) table.getCTTable().getHeaderRowCount();
		if(headerRowCount > 0){
			Node header = parseHeader(tableNode);
			tableNode.addHeader(header);
		}

		ForEachNode forEach = new ForEachNode(sheet);
		tableNode.addLoop(forEach);

		forEach.setRows(new TableExpression(tableName));

		XSSFRow template = sheet.getRow(row.getRowNum() + headerRowCount);
		Node templateNode = sheetParser.createRowNode(sheet, template );
				
		for( int i = 0; i < template.getLastCellNum(); i++){

			buildTableCell(table,templateNode, template.getCell(i, Row.CREATE_NULL_AS_BLANK));

		}
		forEach.getChildren().add(templateNode );

		addTotals(tableNode,templateNode);
		sheetParser.setCurrentRow(end + 1);
	}

    private Node parseHeader(Node parent) {
        Node header = sheetParser.parseRows(parent, row);
        for (Node headerCell : header.getChildren()) {
            if (headerCell instanceof CellNode) {
                CellNode cellNode = (CellNode) headerCell;
                cellNode.setExpression(sheetParser.createHeaderExpression(cellNode.getExpression()));
            }
        }
        return header;
    }

	void buildTableCell(Table table, 
			Node templateNode,	XSSFCell cell) {
		int start = table.getStartCellReference().getCol();
		int end = table.getEndCellReference().getCol();
		int cellCol = cell.getColumnIndex();
		String axis = findAxisCol(cell);

		CTTableColumn[] ctCols  = table.getCTTable().getTableColumns().getTableColumnArray();

		int totalsRowCount = (int) table.getCTTable().getTotalsRowCount();
		String cellVal =  cell.getStringCellValue().trim();		
		XLSXExpression expr = new SimpleExpression( cellVal);

		if(totalsRowCount > 0 && cellCol >= start && cellCol <= end ){
			CTTableColumn totalCol = ctCols[cellCol - start];
			if(totalCol.isSetTotalsRowFunction()){
				expr = new AggregateExpression(expr, Function.valueOf((totalCol.getTotalsRowFunction().toString().toUpperCase())));
			}
		}

		CellNode cellNode =  sheetParser.createCellNode(sheet, cell,cell.getColumnIndex(), expr , templateNode );
		cellNode.setAxis(axis);
		templateNode.getChildren().add(cellNode);


	}
	
	private String findAxisCol(XSSFCell cell) {
		int cellCol = cell.getColumnIndex();
		Iterator<Cell> axixIt = row.cellIterator();
		String axis = null;
		while( axixIt.hasNext() ){
			Cell next = axixIt.next();
			if(cellCol == next.getColumnIndex()){
				axis = next.getStringCellValue();
				break;
			}
		}
		return axis;
	}

	void addTotals(TableNode tableNode, Node templateNode) {

		Table table = tableNode.getTable();
		int totalsRowCount = (int) table.getCTTable().getTotalsRowCount();

		if(totalsRowCount > 0){

			int endRow = table.getEndCellReference().getRow();
			XSSFRow tRow = sheet.getRow(endRow);
			Node totalsRow = sheetParser.createTotalsNode(sheet, tableNode, tRow );
			
			tableNode.addTotals(totalsRow);
			
			int index = table.getStartCellReference().getCol();
			
			insertBlanks(templateNode, tRow, totalsRow, index);
			
			
			
			for( CTTableColumn ctCol  : table.getCTTable().getTableColumns().getTableColumnArray()){
				if(ctCol.isSetTotalsRowFunction()){							
					buildTotalsCell(templateNode, tRow, totalsRow,
							index);
				}else {
					XSSFCell cell = sheet.getRow(endRow).getCell(index, Row.CREATE_NULL_AS_BLANK);
					totalsRow.getChildren().add(sheetParser.createCellNode(sheet, cell, cell.getColumnIndex(), CellExpressionParser.parse(cell), templateNode));
				}
				index++;
			}						
		}
	}

	private void insertBlanks(Node templateNode, XSSFRow tRow, Node totalsRow, int lastCol) {

		for (int i = 0; i < lastCol; i++) {
			XSSFCell cell = tRow.getCell(i, Row.CREATE_NULL_AS_BLANK);
			totalsRow.getChildren().add(sheetParser.createEmtyCell(sheet, cell));
		}
	}

	private void buildTotalsCell(Node templateNode,
                                 XSSFRow tRow, Node totalsRow, int index) {

		CellNode tCellNode = findParentCell(templateNode, index);

		if(tCellNode == null){
			throw new IllegalStateException(INTERNAL_ERROR);
		}

        if(!(tCellNode.getExpression() instanceof AggregateExpression)){
			throw new IllegalStateException(INTERNAL_ERROR);
		}

		XLSXExpression expr = new TotalsExpression(tCellNode);

		XSSFCell cell = tRow.getCell(tCellNode.getCell().getColumnIndex());
		Node cellNode = sheetParser.createCellNode(sheet, cell,cell.getColumnIndex(),expr, templateNode);
		totalsRow.getChildren().add(cellNode);
	}

	private CellNode findParentCell(Node templateNode, int index) {
		CellNode tCellNode = null;

		for(Node node : templateNode.getChildren()){
			if (node instanceof CellNode) {
				XSSFCell nextCell = ((CellNode)node).getCell();
				if( nextCell.getColumnIndex() == index ){
					tCellNode = (CellNode) node;
					break;
				}
			}
		}
		return tCellNode;
	}
}