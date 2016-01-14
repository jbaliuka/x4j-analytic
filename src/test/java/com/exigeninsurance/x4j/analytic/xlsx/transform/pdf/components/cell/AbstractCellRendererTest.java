/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell.BorderMatcher.border;
import static org.apache.poi.ss.usermodel.BorderStyle.DASHED;
import static org.apache.poi.ss.usermodel.BorderStyle.DASH_DOT;
import static org.apache.poi.ss.usermodel.BorderStyle.DASH_DOT_DOT;
import static org.apache.poi.ss.usermodel.BorderStyle.DOTTED;
import static org.apache.poi.ss.usermodel.BorderStyle.DOUBLE;
import static org.apache.poi.ss.usermodel.BorderStyle.HAIR;
import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM;
import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASHED;
import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASH_DOT;
import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASH_DOT_DOTC;
import static org.apache.poi.ss.usermodel.BorderStyle.SLANTED_DASH_DOT;
import static org.apache.poi.ss.usermodel.BorderStyle.THICK;
import static org.apache.poi.ss.usermodel.BorderStyle.THIN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.awt.Color;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.Border;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Point;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Rectangle;

public class AbstractCellRendererTest {

    private AbstractCellRenderer renderer;
    private XSSFCell cell;

    @Mock private PdfCellNode node;
    @Mock private PdfContext context;
	private RenderingContext renderingContext;

	@Before
    public void setup() {
        initMocks(this);
        cell = createCell();
        renderer = new MockRenderer(node);
        when(node.getCellStyle()).thenReturn(cell.getCellStyle());
        when(node.getFillColor()).thenReturn(Color.BLUE);
        when(context.getY()).thenReturn(2.5f);
        when(node.estimateWidth(any(RenderingContext.class))).thenReturn(10f);
		RenderingParameters params = new RenderingParameters(
				new RenderingParameter(RenderingParameter.CELL_VALUE, ""),
				new RenderingParameter(RenderingParameter.ROW_HEIGHT, 10f));
		renderingContext = new RenderingContext(context, params);
    }

    @Test
    public void whenNoBordersAreSetNoBordersShouldBeRendered() throws IOException {
		render();
		verify(context, never()).drawBorder(any(Border.class));
    }

	@Test
    public void verifyBottomBorderIsRenderedInCorrectPosition() throws IOException {
        setBottomBorder(MEDIUM);
		render();
		verify(context).drawBorder(argThat(border(new Point(0, 0), new Point(10, 0), 1)));
    }

    @Test
    public void verifyLeftBorderIsRenderedInCorrectPosition() throws IOException {
        setLeftBorder(MEDIUM);
		render();
		verify(context).drawBorder(argThat(border(new Point(0, 0), new Point(0, 15), 1)));
    }

    @Test
    public void verifyRightBorderIsRenderedInCorrectPosition() throws IOException {
        setRightBorder(MEDIUM);
		render();
		verify(context).drawBorder(argThat(border(new Point(10, 0), new Point(10, 15), 1)));
    }

    @Test
    public void verifyTopBorderIsRenderedInCorrectPosition() throws IOException {
        setTopBorder(MEDIUM);
		render();
		verify(context).drawBorder(argThat(border(new Point(0, 15), new Point(10, 15), 1)));
    }

    @Test
    public void verifyNarrowBordersHaveCorrectWidth() throws IOException {
        verifyBordersAreRenderedUsingCorrectWidth(0.5f, HAIR, THIN, DOTTED, DASH_DOT, DASH_DOT_DOT, DASHED, DOUBLE);
    }

    @Test
    public void verifyMediumBordersHaveCorrectWidth() throws IOException {
        verifyBordersAreRenderedUsingCorrectWidth(1f, MEDIUM, MEDIUM_DASHED, MEDIUM_DASH_DOT, MEDIUM_DASH_DOT_DOTC, SLANTED_DASH_DOT);
    }

    @Test
    public void verifyWideBordersHaveCorrectWidth() throws IOException {
        verifyBordersAreRenderedUsingCorrectWidth(1.5f, THICK);
    }

    @Test
    public void verifyThatCorrectFillIsApplied() throws IOException {
        render();
        verify(context).fill(eq(new Rectangle(0, 0, 10, 15)), eq(Color.BLUE));
    }

    private void verifyBordersAreRenderedUsingCorrectWidth(float expectedWidth, BorderStyle... borderStyles) throws IOException {
        for (BorderStyle borderStyle : borderStyles) {
            setBottomBorder(borderStyle);
			render();
        }
        verify(context, times(borderStyles.length)).drawBorder(argThat(border(new Point(0, 0), new Point(10, 0), expectedWidth)));
    }

	private void render() throws IOException {
		renderer.render(renderingContext);
	}

    private XSSFCell createCell() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row = sheet.createRow(0);
        return row.createCell(0);
    }

    private void setTopBorder(BorderStyle borderStyle) {
        cell.getCellStyle().setBorderTop(borderStyle);
    }

    private void setBottomBorder(BorderStyle borderStyle) {
        cell.getCellStyle().setBorderBottom(borderStyle);
    }

    private void setLeftBorder(BorderStyle borderStyle) {
        cell.getCellStyle().setBorderLeft(borderStyle);
    }

    private void setRightBorder(BorderStyle borderStyle) {
        cell.getCellStyle().setBorderRight(borderStyle);
    }

    private class MockRenderer extends AbstractCellRenderer {

        public MockRenderer(PdfCellNode node) {
            super(node);
        }

        @Override
        protected void drawText(RenderingContext context, Object value) throws IOException {
			PdfContext pdfContext = context.getPdfContext();
			pdfContext.drawText(value.toString(),Color.BLACK, pdfContext.getX(), pdfContext.getY());
        }

        @Override
        protected boolean applyFillAndBorders(RenderingContext context) {
            return true;
        }
    }

}


