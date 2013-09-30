/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.EmptyNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.ForEachNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TextNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.PictureParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;


public abstract class SheetParser {

    public static final POIXMLRelation TABLE = new POIXMLRelation(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml",
            "http://schemas.openxmlformats.org/officeDocument/2006/relationships/table",
            "/xl/tables/table#.xml",
            Table.class
    ) {
    };

    private final Stack<Node> stack = new Stack<Node>();
    private int lastRowNum;

    private XSSFSheet sheet;
    

    private int currentRow;

    private MacroParser macroParser;
    private final List<Table> tables = new ArrayList<Table>();
    private List<Picture> pictures = new ArrayList<Picture>();
    private final boolean showGridLines = false;
    private final boolean showRuler = false;
    private final boolean removeSelection = false;
    private final boolean showRowColHeaders = true;
    private final XLSXStylesTable styles;
    
    private ReportContext reportContext;
    private final Stack<SimpleRange> tableRanges = new Stack<SimpleRange>();
	private final Stack<SimpleRange> openLoops = new Stack<SimpleRange>();
    private Map<String, MergedRegion> mergedCellMap;
    private Collection<MergedRegion> mergedRegions = new ArrayList<MergedRegion>();

    public SheetParser() {
        this(null, null);
    }

    public SheetParser(XLSXStylesTable styles, ReportContext reportContext) {
        this.styles = styles;        
        this.reportContext = reportContext;
    }

    protected abstract Node createPictureNode(XSSFSheet sheet, Picture picture);

    public abstract Node createRowNode(XSSFSheet xssfSheet, Node top, XSSFRow row);

    public abstract CellNode createCellNode(XSSFSheet xssfSheet, XSSFCell cell, int i, XLSXExpression expr, Node parent);

    public abstract Node createTotalsNode(XSSFSheet xssfSheet, TableNode tableNode,
                                          XSSFRow row);

    public XLSXExpression createHeaderExpression(final XLSXExpression expression) {
        return new XLSXExpression() {
            @Override
            public Object evaluate(XLXContext context) throws Exception {
                return WrappingUtil.wrap(String.valueOf(expression.evaluate(context)));
            }

            
        };
    }

    public XSSFSheet getSheet() {
        return sheet;
    }


    public Node parse(XSSFSheet sheet) throws IOException {

        this.sheet = preprocess(sheet);
        collectTables();
        collectMergedCells();
        collectPictures(sheet.getWorkbook().getAllPictures());

        String head = head();

        Node root = new Node(sheet);
        root.getChildren().add(new TextNode(sheet, head));
        if (sheet.iterator().hasNext()) {

            try {

                lastRowNum = findLastRowNum();

                if (lastRowNum >= 0) {

                    stack.push(root);
                    parseBody();
                }

            } catch (Exception e) {
                throw new ReportException(e);
            }
        }

        root.getChildren().add(createTailNode());

        return root;
    }

    private void collectMergedCells() {
        mergedCellMap = new HashMap<String, MergedRegion>();
        if (sheet.getCTWorksheet().getMergeCells() == null) {
            return;
        }
        for (CTMergeCell mergeCell : sheet.getCTWorksheet().getMergeCells().getMergeCellArray()) {
            String ref = mergeCell.getRef();
            String refBase = ref.split(":")[0];
            mergedCellMap.put(refBase, new MergedRegion(ref));
        }
        mergedRegions = mergedCellMap.values();
    }

    protected abstract Node createTailNode();


    private int findLastRowNum() {
        int max = sheet.getLastRowNum();
        for (Picture pict : pictures) {
            if (pict.getFromRow() > max) {
                max = pict.getFromRow();
            }
        }
        return max;
    }

    private void collectPictures(List<XSSFPictureData> pictures) {
        for (int i = 0; i < pictures.size(); i++) {
            try {
                this.pictures.addAll(PictureParser.getSheetPictures(sheet));
            } catch (Exception e) {
                throw new ReportException("Error parsing picture data", e);
            }
        }
    }

    private XSSFSheet preprocess(XSSFSheet sheet) {
        for (CTSheetView v : sheet.getCTWorksheet().getSheetViews().getSheetViewArray()) {
            v.setShowGridLines(showGridLines);
            v.setShowRuler(showRuler);
            v.setTabSelected(sheet.getWorkbook().getSheetIndex(sheet) == 0);
            v.setShowRowColHeaders(showRowColHeaders);

            if (removeSelection) {
                for (CTSelection s : v.getSelectionArray()) {
                    v.removeSelection(0);
                }
            }
        }
        return sheet;
    }


    protected String sheetData(String str) {

        return "";

    }

    protected String tail() {
        return "";
    }


    protected String head() {
        return "";
    }

    private void parseBody() {
        while (getCurrentRow() <= lastRowNum) {
            Node top = getTopNode();
            parseStatement(top);
        }


    }

    private void parseStatement(Node top)  {

        XSSFRow row = sheet.getRow(getCurrentRow());

        if (outOfPrintArea(row)) {
            currentRow += 1;
            return;
        }

        List<Node> pictureNodes = new ArrayList<Node>();
        for (Picture picture : pictures) {
            if (picture.getFromRow() == getCurrentRow()) {
                pictureNodes.add(createPictureNode(sheet, picture));
            }
        }

        if (row == null) {
            Node node = createEmptyRow(sheet, top, getCurrentRow());
            insertPictures(node, pictureNodes);
            pushLeafNode(top, node);
            return;
        }

        for (Iterator<Cell> it = row.cellIterator(); it.hasNext(); ) {
            XSSFCell cell = (XSSFCell) it.next();
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

                String value = cell.getStringCellValue();
                value = value == null ? "" : value.trim();

                if (MacroParser.isMacro(value)) {
                    parseMacro(top, cell, value);
                    return;
                } else {
                    Table table = isTable(cell.getRowIndex(), cell.getColumnIndex());
                    if (table != null) {
                        parseTable(top, table, row);
                        return;
                    }
                }
            }
        }

        Node node = parseRows(top, row);
        insertPictures(node, pictureNodes);
        pushLeafNode(top, node);

    }

    protected boolean outOfPrintArea(XSSFRow row) {
        return false;
    }

    private void parseMacro(Node top, XSSFCell cell, String value) {
        if (MacroParser.isLeafMacro(value)) {
            pushLeafNode(top, macroParser.createMacroNode(value));
        } else if (MacroParser.isBranchMacro(value)) {
            pushBranchNode(macroParser.createMacroNode(value), top);
        } else if (MacroParser.isEndMacro(value)) {
            closeCurrentNode();
        } 
    }

    protected void insertPictures(Node rowNode, List<Node> pictureNodes) {

    }

    
   

    private void parseTable(final Node top, Table table, final XSSFRow row) {

        (new TableParser(this, row, top)).parse(table);

    }


    public Node parseRows(Node top, XSSFRow row) {


        int len = row.getCTRow().getCArray().length;
        Node rowNode = len == 0 ? createEmptyRow(sheet, top, getCurrentRow()) : createRowNode(row.getSheet(), top, row);

        for (int i = 0; i < row.getLastCellNum(); i++) {
            XSSFCell cell = row.getCell(i);
            Node cellNode;
            if (cell == null) {
                cellNode = createEmtyCell(sheet, row.getCell(i, Row.CREATE_NULL_AS_BLANK), rowNode);
            } else {
                cellNode = createCellNode(row.getSheet(), cell, cell.getColumnIndex(), CellExpressionParser.parse(cell), rowNode);
            }
            rowNode.getChildren().add(cellNode);
        }

        return rowNode;
    }

    private void pushBranchNode(Node node, Node parent) {
        parent.getChildren().add(node);
        if (node instanceof ForEachNode) {
            SimpleRange range = new SimpleRange();
			range.setFirst(getCurrentRow() + 1);
			openLoops.push(range);
        }
        stack.push(node);
        setCurrentRow(getCurrentRow() + 1);
    }

	private void closeCurrentNode() {
		Node top = stack.pop();
		if (top instanceof ForEachNode) {
			SimpleRange currentRange = openLoops.pop();
			currentRange.setLast(getCurrentRow() - 1);
			tableRanges.push(currentRange);
		}
		setCurrentRow(getCurrentRow() + 1);
	}

    private void pushLeafNode(Node top, Node macro) {
        top.getChildren().add(macro);
        setCurrentRow(getCurrentRow() + 1);
    }

    private void collectTables() {
        for (POIXMLDocumentPart p : sheet.getRelations()) {
            if (p.getPackageRelationship().getRelationshipType().equals(XSSFRelation.TABLE.getRelation())) {
                Table table = (Table) p;
                tables.add(table);
                if (getStyles() != null) {
                    String style = getStyles().getDefaultTableStyle();
                    table.getCTTable().getTableStyleInfo().setName(style);
                }

            }
        }
    }

    private XLSXStylesTable getStyles() {
        return styles;
    }

    public Table isTable(int row, int col) {

        for (Table table : tables) {
            if (table.getStartCellReference().getRow() == row &&
                    table.getStartCellReference().getCol() == col
                    ) {
                return table;
            }
        }

        return null;
    }


    public boolean isTableDataRow(int row) {
        return tables(row, 1) || forEachLoops(row);
    }

	protected boolean isTableRow(int row) {
		return tables(row, 0) || forEachLoops(row);
	}

    private boolean forEachLoops(int row) {
		if (!openLoops.empty()) {
			return true;
		}
		if (tableRanges.empty()) {
			return false;
		}
		for (SimpleRange range : tableRanges) {
			if (row >= range.getFirst() && row <= range.getLast()) {
				return true;
			}
		}
		return false;
    }

    private boolean tables(int row, int rowOffset) {
        for (Table table : getTables()) {
            int dataStart = table.getStartCellReference().getRow() + rowOffset;
            int tableEnd = table.getEndCellReference().getRow();
            if (dataStart <= row && tableEnd >= row) {
                return true;
            }
        }
        return false;
    }

    public List<Table> getTables() {
        return tables;
    }

    public TableNode createTableNode(XSSFSheet sheet, Node top, Table table) {

        return new TableNode(sheet, table);
    }

    public Node createEmptyRow(XSSFSheet sheet, Node parent, int row) {
        return new EmptyNode(sheet);
    }

    public Node createEmtyCell(XSSFSheet sheet, XSSFCell cell, Node parent) {

        return new Node(sheet) {
            @Override
            public void process(XLXContext context) throws Exception {

            }
        };
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    

    public int getCurrentRow() {
        return currentRow;
    }

    public ReportContext getReportContext() {
        return reportContext;
    }

    public void setReportContext(ReportContext reportContext) {
        this.reportContext = reportContext;
    }

    private Node getTopNode() {
        return stack.peek();
    }

    public Map<String, MergedRegion> getMergedCells() {
        return mergedCellMap;
    }

    public Collection<MergedRegion> getMergedRegions() {
        return mergedRegions;
    }

    public void setMacroParser(MacroParser macroParser) {
        this.macroParser = macroParser;
    }
}
