/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.NoOpExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoOpProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.NoStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableHeaderStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.BasicCellHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.EmptyCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.MeasuringProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.PdfTableCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellHeigthEstimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.WrappingCellRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;

public class PdfSheetParserTest {

	private ReportContext reportContext;
	private PdfSheetParser parser;
	private ComponentFactory preprocessingFactory;

	private PdfContainer parent;

	@Before
	public void setUp() throws Exception {
		reportContext = new ReportContext(null);
		parser = new PdfSheetParser(reportContext);
		preprocessingFactory = new PreProcessingComponentFactory();
	}

	@Test
    public void testRanges() throws Exception {
		using("/testRanges.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				Node root = parser.parse(sheet);
				assertThat(root.getChildren(), hasSize(7));
				assertEquals(0, ((PdfContainer) root.getChildren().get(1)).getVerticalRange().getFirst());
				assertEquals(1, ((PdfContainer) root.getChildren().get(2)).getVerticalRange().getFirst());
				assertEquals(2, ((PdfContainer) root.getChildren().get(3)).getVerticalRange().getFirst());
				assertEquals(3, ((PdfContainer) root.getChildren().get(4)).getVerticalRange().getFirst());
				assertEquals(4, ((PdfContainer) root.getChildren().get(5)).getVerticalRange().getFirst());
			}
		});
    }

	@Test
	public void testForLoopRanges() throws Exception {
		using("/forLoops.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				prepareParser(sheet);
				assertThat(parser.isTableDataRow(1), is(true));
				assertThat(parser.isTableDataRow(5), is(true));
				assertThat(parser.isTableDataRow(9), is(true));
				assertThat(parser.isTableDataRow(11), is(true));

				assertThat(parser.isTableDataRow(0), is(false));
				assertThat(parser.isTableDataRow(3), is(false));
				assertThat(parser.isTableDataRow(7), is(false));
				assertThat(parser.isTableDataRow(14), is(false));
			}
		});
	}

	@Test
	public void testTableHeadersPreprocessing() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				setPreprocessingMode(sheet);
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(0).getCell(0);
				PdfCellNode headerCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellDoesntExpandColumn(headerCell);
			}
		});
	}

	@Test
	public void testTableHeaders() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(0).getCell(0);
				PdfCellNode headerCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellIsWrapped(headerCell);
				cellHasHeaderStyle(headerCell);
			}
		});
	}

	@Test
	public void testTableDataPreprocessing() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				setPreprocessingMode(sheet);
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(1).getCell(0);
				PdfCellNode tableCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellExpandsColumn(tableCell);
			}
		});
	}

	@Test
	public void testTableData() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(1).getCell(0);
				PdfCellNode tableCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellUsesDefaultRendering(tableCell);
				cellHasTableStyle(tableCell);
			}
		});
	}

	@Test
	public void testEmptyCellPreprocessing() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				setPreprocessingMode(sheet);
				prepareParser(sheet);
				sheet.createRow(5);
				XSSFCell cell = sheet.getRow(5).getCell(10, Row.CREATE_NULL_AS_BLANK);
				PdfCellNode pdfCell = (PdfCellNode) parser.createEmtyCell(sheet, cell);

				cellDoesntExpandColumn(pdfCell);
			}
		});
	}

	@Test
	public void testEmptyCell() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				prepareParser(sheet);
				sheet.createRow(5);
				XSSFCell cell = sheet.getRow(5).getCell(10, Row.CREATE_NULL_AS_BLANK);
				PdfCellNode pdfCell = (PdfCellNode) parser.createEmtyCell(sheet, cell);

				assertThat(pdfCell.getStylingComponent(), instanceOf(NoStyle.class));
				assertThat(pdfCell.getRenderer(), instanceOf(EmptyCellRenderer.class));
			}
		});
	}

	@Test
	public void testWrappedTableCellPreprocessing() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				setPreprocessingMode(sheet);
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(1).getCell(0);
				cell.getCellStyle().setWrapText(true);
				PdfCellNode pdfCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellExpandsColumn(pdfCell);
			}
		});
	}

	@Test
	public void testWrappedTableCell() throws Exception {
		using("/basiclong.xlsx", new WithSheet() {
			@Override
			public void execute(XSSFSheet sheet) throws Exception {
				prepareParser(sheet);
				XSSFCell cell = sheet.getRow(1).getCell(0);
				cell.getCellStyle().setWrapText(true);
				PdfCellNode pdfCell = (PdfCellNode) parser.createCellNode(sheet, cell, 0, new NoOpExpression(cell), parent);

				cellIsWrapped(pdfCell);
				cellHasTableStyle(pdfCell);
			}
		});
	}

	private void cellHasTableStyle(PdfCellNode cell) {
		assertThat(cell.getStylingComponent(), instanceOf(TableStyle.class));
	}

	private void cellUsesDefaultRendering(PdfCellNode cell) {
		assertThat(cell.getHeigthEstimator(), instanceOf(BasicCellHeigthEstimator.class));
		assertThat(cell.getRenderer(), instanceOf(PdfTableCellRenderer.class));
	}

	private void cellExpandsColumn(PdfCellNode cell) {
		assertThat(cell.getProcessor(), instanceOf(MeasuringProcessor.class));
	}

	private void cellDoesntExpandColumn(PdfCellNode cell) {
		assertThat(cell.getProcessor(), instanceOf(NoOpProcessor.class));
	}

	private void cellHasHeaderStyle(PdfCellNode cell) {
		assertThat(cell.getStylingComponent(), instanceOf(TableHeaderStyle.class));
	}

	private void cellIsWrapped(PdfCellNode cell) {
		assertThat(cell.getHeigthEstimator(), instanceOf(WrappingCellHeigthEstimator.class));
		assertThat(cell.getRenderer(), instanceOf(WrappingCellRenderer.class));
	}

	private void prepareParser(XSSFSheet sheet) throws IOException {
		parser.setMacroParser(new MacroParser(new MacroNodeFactoryImpl(sheet)));
		parser.parse(sheet);
	}

	private void setPreprocessingMode(XSSFSheet sheet) {
		parser.setComponentFactory(preprocessingFactory);
		parser.setMacroParser(new MacroParser(new PreprocessingMacroFactory(sheet)));
	}

	private void using(String sheetPath, WithSheet actions) throws Exception {
		InputStream input = getClass().getResourceAsStream(sheetPath);
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			XSSFSheet sheet = workbook.getSheetAt(0);
			actions.execute(sheet);
		} finally {
			input.close();
		}
	}

	private interface WithSheet {
		public void execute(XSSFSheet sheet) throws Exception;
	}

}
