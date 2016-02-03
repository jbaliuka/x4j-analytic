/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableHeaderStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.MergedCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;
import com.exigeninsurance.x4j.analytic.xlsx.utils.XSSFSheetHelper;


public class PdfSheetParser extends SheetParser {

    private int [] repeatingRows;
    private List<PdfContainer> repeatingItems = new ArrayList<PdfContainer>();
    private ComponentFactory componentFactory = new ComponentFactoryImpl();

    public PdfSheetParser(ReportContext reportContext) {
		super(reportContext);
	}

	public PdfSheetParser() {
		super(null);
	}

	public CellNode createCellNode(XSSFSheet xssfSheet, XSSFCell cell,
                                   int i, XLSXExpression expr, Node parent) {
        PdfCellNode cellNode = createCellNode(xssfSheet, cell, expr);
        cellNode.setParent((PdfContainer) parent);
        return cellNode;
	}

    private PdfCellNode createCellNode(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
		PdfCellNode cellNode;
		if (isTableHeader(cell.getRowIndex())) {
			cellNode = componentFactory.createHeaderCell(xssfSheet, cell, expr);
		} else {
			cellNode = createCell(xssfSheet, cell, expr);
			if (isWrapped(cell) && !getMergedCells().containsKey(cellNode.getAbsoluteRef())) {
				componentFactory.makeCellWrappable(cellNode);
			}
		}
        setStyle(cell, cellNode);
        transformCellIfMerged(cellNode);
        return cellNode;
    }

	private PdfCellNode createCell(XSSFSheet xssfSheet, XSSFCell cell, XLSXExpression expr) {
		return isTableDataRow(cell.getRow().getRowNum()) ?
				componentFactory.createTableCell(xssfSheet, cell, expr) :
				componentFactory.createCell(xssfSheet, cell, expr);
	}

    private boolean isWrapped(XSSFCell cell) {
        return cell.getCellStyle().getWrapText();
    }

    private void setStyle(XSSFCell cell, PdfCellNode cellNode) {
        if (isTableHeader(cell.getRow().getRowNum())) {
            cellNode.setStylingComponent(new TableHeaderStyle());
        }
        else {
            cellNode.setStylingComponent(new TableStyle());
        }
    }

    private void transformCellIfMerged(PdfCellNode cell) {
		if (isMergedCell(cell)) {
			cell.setRenderer(new MergedCellRenderer(cell));
		}
	}
	
	private boolean isMergedCell(PdfCellNode cell) {
        for (MergedRegion region : getMergedRegions()) {
            if (region.isInRegion(cell.getCell().getRowIndex(), cell.getCell().getColumnIndex())) {
                return true;
            }
        }
		return false;
	}
	
	@Override
    public Node createRowNode(XSSFSheet xssfSheet, Node top, XSSFRow row) {
		if (isTableHeader(row.getRowNum())) {
			return componentFactory.createTableHeaderRow(xssfSheet, createOneRowRange(row.getRowNum()));
		}
        PdfContainer tableRow = componentFactory.createTableRow(xssfSheet, createOneRowRange(row.getRowNum()));
        if (isRepeatingRow(row.getRowNum(), xssfSheet)) {
            repeatingItems.add(tableRow);
        }
		return tableRow;
	}

    private boolean isRepeatingRow(int rowNum, XSSFSheet sheet) {
        int[] repeating = getRepeatingRows(sheet);
        if (repeating.length == 0) {
            return false;
        }

        for (int i = repeating[0]; i < repeating[repeating.length -1] + 1; i++) {
            if (rowNum == i) {
                return true;
            }
        }
        return false;
    }

    @Override
	public TableNode createTableNode(XSSFSheet sheet, Node top, Table table) {
		return new PdfTableNode(sheet, top, table);
	}

	@Override
	protected Node createPictureNode(XSSFSheet sheet,
                                     Picture picture) {

        return componentFactory.createPictureNode(sheet, picture);
	}

	@Override
	public Node createTotalsNode(XSSFSheet xssfSheet, TableNode tableNode,
			XSSFRow row) {
		return componentFactory.createTableRow(xssfSheet, createOneRowRange(row.getRowNum()));
	}

    @Override
    protected Node createTailNode() {
        return new Node(getSheet());
    }

    @Override
	public Node createEmtyCell(XSSFSheet sheet, XSSFCell cell, Node parent) {
		
		return componentFactory.createEmptyCell(sheet, cell);
	}	
	
	@Override
	public Node createEmptyRow(XSSFSheet sheet, Node parent, int row) {
		return componentFactory.createEmptyRow(sheet, createOneRowRange(row));
	}

    private Range createOneRowRange(int row) {
		return new SimpleRange(row, row);
	}

	@Override
	protected void insertPictures(Node rowNode, List<Node> pictureNodes) {
        insertPictures(rowNode, pictureNodes, findLastColInRow(rowNode));
	}

    @Override
    protected boolean outOfPrintArea(XSSFRow row) {
        return row == null ? false : !XSSFSheetHelper.rowIsInPrintArea(getSheet(), row.getRowNum());
    }

    private void insertPictures(Node rowNode, List<Node> pictureNodes, int last) {
        XSSFRow row = getSheet().getRow(getCurrentRow());
        if (row == null) {
            row = findNonNullRow();
        }

        for (Node pictureNode : pictureNodes) {
            if (pictureNode instanceof PdfPictureNode) {
                for (int i = last + 1; i < ((PdfPictureNode) pictureNode).getPictureData().getFromCol(); i++) {
                    XSSFCell xssfCell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
                    rowNode.getChildren().add(createEmtyCell(getSheet(), xssfCell, rowNode));
                }
                rowNode.getChildren().add(pictureNode);
                ((PdfPictureNode) pictureNode).setParent((PdfContainer) rowNode);
                last = ((PdfPictureNode) pictureNode).getPictureData().getFromCol();
            }
        }
    }

    private int findLastColInRow(Node rowNode) {
        int last = 0;

        for (Node cell : rowNode.getChildren()) {
			if (cell instanceof CellNode) {
				int columnIndex = ((CellNode) cell).getCell().getColumnIndex();
				if (columnIndex > last) {
					last = columnIndex;
				}
			}
		}
        return last;
    }

    private boolean isTableHeader(int row) {
        for(Table table: getTables()){
            int tableStartRow = table.getStartCellReference().getRow();
            if(tableStartRow == row) {
                return true;
            }
        }

        return false;
    }
	
	private XSSFRow findNonNullRow() {
		for (int i = 0; i < getSheet().getLastRowNum(); i++) {
			if (getSheet().getRow(i) != null) {
				return getSheet().getRow(i);
			}
		}
		return null;
	}

    private int[] getRepeatingRows(XSSFSheet sheet) {
        if (repeatingRows == null) {
            repeatingRows = XSSFSheetHelper.getRepeatingRows(sheet.getWorkbook(), sheet.getWorkbook().getSheetIndex(sheet));
        }
        return repeatingRows;
    }

    public List<PdfContainer> getRepeatingItems() {
        return repeatingItems;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }
}
