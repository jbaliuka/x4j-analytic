/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.TIMES_ROMAN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;

public class PdfTableCellRendererTest {

	protected PdfContext context = mock(PdfContext.class);
	protected PdfCellNode cellNode = mock(PdfCellNode.class);
	protected XSSFCellStyle cellStyle = mock(XSSFCellStyle.class);

	private Renderer tableCellRenderer = new PdfTableCellRenderer(cellNode);

    @Before
    public void setup() {
		when(context.getX()).thenReturn(0f);
		when(context.getY()).thenReturn(0f);
		when(cellNode.formatValue(eq(context), any())).thenAnswer(MethodArgument(1));
		when(cellNode.getMaxFontHeight()).thenReturn(10f);
		when(cellNode.findFontSize()).thenReturn(10);
		when(cellNode.getHorizontalAlignment()).thenReturn(Alignment.LEFT);
		when(cellNode.getVerticalAlignment()).thenReturn(CellStyle.VERTICAL_BOTTOM);
		when(cellNode.getCellStyle()).thenReturn(cellStyle);
		when(cellNode.getFont()).thenReturn(TIMES_ROMAN);
	}

    @Test
    public void testDrawText() throws IOException {
		RenderingParameters renderingParameters = new RenderingParameters(
				new RenderingParameter(RenderingParameter.CELL_VALUE, "ignored"),
				new RenderingParameter(RenderingParameter.ROW_HEIGHT, 10f));
		RenderingContext renderingContext = new RenderingContext(context, renderingParameters);
		when(cellNode.estimateWidth(renderingContext)).thenReturn(90f);
		tableCellRenderer.render(renderingContext);
		verify(context).setTextOptions(10, TIMES_ROMAN, Font.U_NONE);
        verify(context).drawText(anyString(),any(Color.class), eq(0f), eq(0f));
    }

	protected Answer<String> MethodArgument(final int argument) {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocationOnMock) throws Throwable {
				return invocationOnMock.getArguments()[argument].toString();
			}
		};
	}
}