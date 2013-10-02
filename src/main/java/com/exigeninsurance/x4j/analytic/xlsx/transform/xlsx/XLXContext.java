/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
 */


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.TemplateResolver;
import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Money;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.CellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.AlignmentFinder;
import com.exigeninsurance.x4j.analytic.xlsx.transform.CaseInsensitiveContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.PdfStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SST;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.UTF8OutputStream;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfStylesParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;
import com.exigeninsurance.x4j.analytic.xlsx.utils.FormatUtil;
import com.exigeninsurance.x4j.analytic.xlsx.utils.WrappingUtil;


public class XLXContext {

	public static final int NOT_A_TABLE = -1;
	private static final String CONTEXT_VARIABLE_NAME = "renderer";
	private static final String STYLES_XML = "/tableStyles.xml";

	protected long linkStyle;
	protected int tableHeaderStyle = -1;
	protected boolean header;

	private UTF8OutputStream out;
	private ReportContext reportContext;
	private ReportDataProvider dataProvider;
	private int currentRow;
	private int[] repeatingRows = new int[]{};
	private SST sst;
	private CaseInsensitiveContext exprContext;

	private TemplateResolver templateProvider;
	private XSSFSheet sheet;
	private int tableStart;
	private int tableId;

	private Map<Integer, Double> columnWidths = new HashMap<Integer, Double>();

	private FormatProvider formatProvider;
	private final Map <Integer, String> tableStylesMap = new HashMap<Integer, String>();
	private PdfStylesTable stylesTable;
	

	private Map<String, MergedRegion> mergedCells = new HashMap<String, MergedRegion>();
	private List<MergedRegion> newMergedCells = new ArrayList<MergedRegion>();
	private List<Table> tables = new ArrayList<Table>();
	private FormatUtil formatUtil;
	private XLSXStyleUtil xlsxStyleUtil;
	private AlignmentFinder alignmentFinder;

	public XLXContext(SST sst,
			XSSFSheet sheet,
			ReportContext reportContext, 
			OutputStream out) {
		this.out = new UTF8OutputStream(new BufferedOutputStream(out));
		this.reportContext = reportContext;
		setSst(sst);
		this.sheet = sheet;
	}

	public ReportDataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(ReportDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public ReportContext getReportContext() {
		return reportContext;
	}

	public void write(String bytes) throws IOException{
		out.writeUTF(bytes);
	}

	public void write(byte[] bytes) throws IOException{
		out.write(bytes);
	}

	public int getCurrentRow() {
		return currentRow;
	} 

	public void setCurrentRow(int currentRow){
		this.currentRow = currentRow;
	}

	public void write(long s) throws IOException {
		write(Long.toString(s));
	}

	public void write(byte s) throws IOException {
		out.write(s);
	}

	public void nextRow() {
		currentRow++;	
	}

	public void nextRow(int row) {
		if (isStartOfRepeatingRowRange(row)) {
			setRepeatingRows(row);
		}
		nextRow();
	}

	private boolean isStartOfRepeatingRowRange(int row) {
		return repeatingRows.length != 0 && repeatingRows[0] == row;
	}

	private void setRepeatingRows(int row) {
		int diff = currentRow - row;
		int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
		sheet.getWorkbook().setRepeatingRowsAndColumns(sheetIndex, -1, -1, repeatingRows[0] + diff, repeatingRows[repeatingRows.length - 1] + diff);
	}

	public void flush() throws IOException{
		out.flush();
	}

	public void setSst(SST sst) {
		this.sst = sst;
	}

	public SST getSst() {
		return sst;
	}


	public void write(Object v) throws IOException {
		if(v != null){
			write( v.toString());
		}
	}

	public CaseInsensitiveContext getExpresionContext(){
		if(exprContext == null){
			exprContext = new CaseInsensitiveContext();
			exprContext.getVars().putAll(reportContext.getParameters());
			exprContext.getVars().put(CONTEXT_VARIABLE_NAME, this);
		}
		return exprContext;
	}

	public void setTemplateProvider(TemplateResolver templateProvider) {
		this.templateProvider = templateProvider;
	}

	public TemplateResolver getTemplateProvider(){
		return templateProvider;
	}

	public long createStyle(Object value, XLSXCellNode node) {


		if (value instanceof Link) {
			return linkStyle;
		}

		if(value instanceof Date){
			return xlsxStyleUtil.getLocalizedDateStyle(value, node);
		}
		if (value instanceof Money) {
			return xlsxStyleUtil.getLocalizedCurrencyStyle((Money) value, node);
		}

		return node.getStyle();
	}

	public void setStyles(XLSXStylesTable styles) {

		formatUtil = new FormatUtil(formatProvider, reportContext.getLocale());
		xlsxStyleUtil = new XLSXStyleUtil(sheet, formatProvider, reportContext.getLocale(), getAlignmentFinder());
	}



	public void parseTableStyles(XSSFWorkbook workbook) throws Exception {
		parseDefaultStyles();
		parseTemplateStyles(workbook);

	}

	private void parseDefaultStyles() throws IOException {
		InputStream stream = getClass().getResourceAsStream(STYLES_XML);
		try {
			PdfStylesParser pdfStylesParser = new PdfStylesParser(stream);
			stylesTable = pdfStylesParser.produceStylesTable();
		} finally {
			stream.close();
		}
	}

	private void parseTemplateStyles(XSSFWorkbook workbook) throws IOException {
		PdfStylesParser pdfStylesParser;
		PackagePart part = workbook.getStylesSource().getPackagePart();
		InputStream stream = null;
		try {
			stream = part.getInputStream();
			pdfStylesParser = new PdfStylesParser(stream);
		}
		finally {
			if (stream != null) {
				stream.close();
			}
		}
		PdfStylesTable currentStyles = pdfStylesParser.produceStylesTable();
		stylesTable.getTableStyles().putAll(currentStyles.getTableStyles());
		
	}



	public TableStyle findTableStyle(int tableId) {
		String styleName = tableStylesMap.get(tableId);

		if (styleName == null) {
			return null;
		}


		
		if (stylesTable.getTableStyles().containsKey(styleName)) {
			return stylesTable.getTableStyles().get(styleName);
		}
		
		styleName += PdfStylesParser.DUPLICATE_SUFFIX;
		
		if (stylesTable.getTableStyles().containsKey(styleName)) {
			return stylesTable.getTableStyles().get(styleName);
		}
		else {
			return null;
		}

	}

	public PdfStyle findStyle(TableStyle tableStyle, CellNode cell) {
		if (stylesTable != null) {
			int cellRow = cell.getCell().getRowIndex();
			if (cellRow == tableStart) {
				return tableStyle.getHeaderRowStyle();
			}
			else {
				return tableStyle.getWholeTableStyle();
			}
		}

		return null;
	}

	public void setTableStartHeader() {
		header = true;
	}

	public void endTableHeader() {
		header = false;
	}

	public UTF8OutputStream getOut() {
		return out;
	}

	public void setOut(UTF8OutputStream out) {
		this.out = out;
	}

	public XSSFSheet getSheet() {
		return sheet;
	}

	public void setSheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}

	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public Map<Integer, String> getTableStylesMap() {
		return tableStylesMap;
	}

	public void setTableStart(int tableStart) {
		this.tableStart = tableStart;
	}



	public String defaultDateFormat(Date value) {
		return formatUtil.defaultDateFormat(value);
	}

	public String formatMoney(Money money) {
		return formatUtil.formatMoney(money);
	}

	public void setFormatProvider(FormatProvider formatProvider) {
		this.formatProvider = formatProvider;
	}

	public String dateTimeFormat(Date value) {
		return formatUtil.dateTimeFormat(value);
	}

	public Map<String, MergedRegion> getMergedCells() {
		return mergedCells;
	}

	public void setMergedCells(Map<String, MergedRegion> mergedCells) {
		this.mergedCells = mergedCells;
	}

	public boolean isCellMerged(String ref) {
		return mergedCells.containsKey(ref);
	}

	public MergedRegion getMergedRegion(XSSFCell cell) {
		String ref = cell.getReference();
		if (isCellMerged(ref)) {
			return mergedCells.get(ref);
		}
		Collection<MergedRegion> regions = mergedCells.values();
		for (MergedRegion region : regions) {
			if (region.isInRegion(cell.getRowIndex(), cell.getColumnIndex())) {
				return region;
			}
		}
		return null;
	}

	public List<MergedRegion> getNewMergedCells() {
		return newMergedCells;
	}

	// Do not remove, it's unused in the code, but can be invoked from templates directly
	public void addPageBreak() throws Exception {

	}

	public void setRepeatingRows(int[] repeatingRows) {
		this.repeatingRows = repeatingRows;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void processTable(Table table) {
		tables.add(table);
	}

	public void reportCellWidth(XSSFCell cell,String value) {
		for (String s : value.split(WrappingUtil.EXCEL_BREAK)) {
			if (!s.isEmpty()) {
				updateColumnWidth(cell, s);
			}
		}
	}

	private void updateColumnWidth(XSSFCell cell, String value) {
		double width = CellWidthEstimator.getCellWidth(cell, value);
		int col = cell.getColumnIndex();
		if (columnWidths.containsKey(col)) {
			columnWidths.put(col, Math.max(width, columnWidths.get(col)));
		} else {
			columnWidths.put(col, Math.max(width, sheet.getColumnWidth(col) / 256f));
		}
	}

	public Map<Integer, Double> getColumnWidths() {
		return columnWidths;
	}

	private AlignmentFinder getAlignmentFinder() {
		if (alignmentFinder == null) {
			alignmentFinder = new AlignmentFinder(reportContext);
		}
		return alignmentFinder;
	}

	public Alignment determineAlignment(Object value, XSSFCell cell) {
		return getAlignmentFinder().determineAlignment(value, cell.getCellStyle());
	}
}
