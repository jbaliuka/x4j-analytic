/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;

public class PdfLayoutTransformerTest {

    private ComponentFactoryImpl componentFactory;

    @Before
    public void setUp() throws Exception {
        componentFactory = new ComponentFactoryImpl();
    }

    @Test
	public void testBasicWrapping() throws InvalidFormatException, IOException {
		ReportContext reportContext = new ReportContext(null);
		SheetParser parser = new PdfSheetParser( reportContext);

		XSSFWorkbook book = new XSSFWorkbook();		 
		XSSFSheet sheet = book.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("a");
		row.createCell(1);
		
		row = sheet.createRow(1);
		row.createCell(0);
		row.createCell(1);
		
		CellRangeAddress cra = new CellRangeAddress(0, 1, 0, 1);
		sheet.addMergedRegion(cra);
		
		Node root = parser.parse(sheet);
		PdfContext context = new PdfContext(null, sheet, reportContext);
		context.setMergedCells(parser.getMergedCells());
		PdfLayoutTransformer transformer = new PdfLayoutTransformer(context, componentFactory);
		
		transformer.transform(root);
		
		assertEquals(3, root.getChildren().size());
		assertTrue(root.getChildren().get(1) instanceof PdfContainer);
		
		PdfContainer wrapper = (PdfContainer) root.getChildren().get(1);
		assertEquals(2, wrapper.getChildren().size());
		assertEquals(0, wrapper.getVerticalRange().getFirst());
		assertEquals(1, wrapper.getVerticalRange().getLast());
		
		PdfContainer firstChild = (PdfContainer) wrapper.getChildren().get(0);
		PdfContainer secondChild = (PdfContainer) wrapper.getChildren().get(1);
		
		int firstChildRow =  firstChild.getVerticalRange().getFirst();
		int secondChildRow = secondChild.getVerticalRange().getFirst();
		
		assertEquals(0, firstChildRow);
		assertEquals(1, secondChildRow);
		
		assertEquals(wrapper, firstChild.getParent());
		assertEquals(wrapper, secondChild.getParent());
	}
	
	@Test
	public void testSeparateMultipleMergedRegions() throws InvalidFormatException, IOException {
		ReportContext reportContext = new ReportContext(null);
		SheetParser parser = new PdfSheetParser(reportContext);

		XSSFWorkbook book = new XSSFWorkbook();		 
		XSSFSheet sheet = book.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("a");
		row.createCell(1);
		
		row = sheet.createRow(1);
		row.createCell(0);
		row.createCell(1);
		
		CellRangeAddress cra = new CellRangeAddress(0, 1, 0, 1);
		sheet.addMergedRegion(cra);
		
		row = sheet.createRow(2);
		row.createCell(0);
		row.createCell(1);
		
		row = sheet.createRow(3);
		row.createCell(0);
		row.createCell(1);
		
		sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 1));
		
		Node root = parser.parse(sheet);
		PdfContext context = new PdfContext(null, sheet, reportContext);
		context.setMergedCells(parser.getMergedCells());
		PdfLayoutTransformer transformer = new PdfLayoutTransformer(context, componentFactory );
		
		transformer.transform(root);
		
		assertEquals(4, root.getChildren().size());
		
		PdfContainer firstWrapper = (PdfContainer) root.getChildren().get(1);
		PdfContainer secondWrapper = (PdfContainer) root.getChildren().get(2);
		
		assertEquals(0, firstWrapper.getVerticalRange().getFirst());
		assertEquals(1, firstWrapper.getVerticalRange().getLast());
		
		assertEquals(2, secondWrapper.getVerticalRange().getFirst());
		assertEquals(3, secondWrapper.getVerticalRange().getLast());
	}
	
	@Test
	public void testMultipleMergedRegionsStartingOnSameRow() throws InvalidFormatException, IOException {
		ReportContext reportContext = new ReportContext(null);
		SheetParser parser = new PdfSheetParser(reportContext);

		XSSFWorkbook book = new XSSFWorkbook();		 
		XSSFSheet sheet = book.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		row.createCell(0);
		row.createCell(1);
		row.createCell(2);
		row.createCell(3);
		
		row = sheet.createRow(1);
		row.createCell(0);
		row.createCell(1);
		row.createCell(2);
		row.createCell(3);
		
		CellRangeAddress cra = new CellRangeAddress(0, 1, 0, 1);
		sheet.addMergedRegion(cra);
		
		row = sheet.createRow(2);
		row.createCell(2);
		row.createCell(3);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 3));
		
		Node root = parser.parse(sheet);
		PdfContext context = new PdfContext(null, sheet, reportContext);
		context.setMergedCells(parser.getMergedCells());
		PdfLayoutTransformer transformer = new PdfLayoutTransformer(context, componentFactory );
		
		transformer.transform(root);
		
		assertEquals(3, root.getChildren().size());
		
		PdfContainer wrapper = (PdfContainer) root.getChildren().get(1);
		assertEquals(0, wrapper.getVerticalRange().getFirst());
		assertEquals(2, wrapper.getVerticalRange().getLast());
	}
	
	@Test
	public void testMultipleOverlappingRegions() throws InvalidFormatException, IOException {
		ReportContext reportContext = new ReportContext(null);
		SheetParser parser = new PdfSheetParser(reportContext);

		XSSFWorkbook book = new XSSFWorkbook();		 
		XSSFSheet sheet = book.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		row.createCell(0);
		row.createCell(1);
		
		row = sheet.createRow(1);
		row.createCell(0);
		row.createCell(1);
		row.createCell(2);
		row.createCell(3);
		
		CellRangeAddress cra = new CellRangeAddress(0, 1, 0, 1);
		sheet.addMergedRegion(cra);
		
		row = sheet.createRow(2);
		row.createCell(2);
		row.createCell(3);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 3));
		
		Node root = parser.parse(sheet);
		PdfContext context = new PdfContext(null, sheet, reportContext);
		context.setMergedCells(parser.getMergedCells());
		PdfLayoutTransformer transformer = new PdfLayoutTransformer(context , componentFactory);
		
		transformer.transform(root);
		
		assertEquals(3, root.getChildren().size());
		
		PdfContainer wrapper = (PdfContainer) root.getChildren().get(1);
		assertEquals(3, wrapper.getChildren().size());
		assertEquals(0, wrapper.getVerticalRange().getFirst());
		assertEquals(2, wrapper.getVerticalRange().getLast());
	}
	
	@Test
	public void testNestedChildren() throws InvalidFormatException, IOException {
		ReportContext reportContext = new ReportContext(null);
		SheetParser parser = new PdfSheetParser(reportContext);


		XSSFWorkbook book = new XSSFWorkbook();		 
		XSSFSheet sheet = book.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("#for row in rows");
		
		row = sheet.createRow(1);
		row.createCell(0);
		row.createCell(1);
		
		row = sheet.createRow(2);
		row.createCell(0);
		row.createCell(1);
		
		row = sheet.createRow(3);
		cell = row.createCell(0);
		cell.setCellValue("#end");

        parser.setMacroParser(new MacroParser(new MacroNodeFactoryImpl(sheet)));
		
		CellRangeAddress cra = new CellRangeAddress(1, 2, 0, 1);
		sheet.addMergedRegion(cra);
		
		Node root = parser.parse(sheet);
		PdfContext context = new PdfContext(null, sheet, reportContext);
		context.setMergedCells(parser.getMergedCells());
		PdfLayoutTransformer transformer = new PdfLayoutTransformer(context, componentFactory );
		
		transformer.transform(root);
		
		assertEquals(3, root.getChildren().size());
		Node forEach = root.getChildren().get(1);
		assertEquals(1, forEach.getChildren().size());
		
		PdfContainer wrapper = (PdfContainer) forEach.getChildren().get(0);
		assertEquals(2, wrapper.getChildren().size());
		assertEquals(1, wrapper.getVerticalRange().getFirst());
		assertEquals(2, wrapper.getVerticalRange().getLast());
		
		int firstChildRow = ((PdfContainer) wrapper.getChildren().get(0)).getVerticalRange().getFirst();
		int secondChildRow = ((PdfContainer) wrapper.getChildren().get(1)).getVerticalRange().getFirst();
		
		assertEquals(1, firstChildRow);
		assertEquals(2, secondChildRow);
	}
}
