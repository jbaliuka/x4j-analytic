/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Rectangle;

public class MergedCellRendererTest extends CellRendererTestFixture {

    @Mock private PdfContainer nodeParent;

    @Test
    public void testPointerMove() throws IOException {
        render();
        verify(context).movePointerBy(20, 0);
    }

    @Test
    public void testFilling() throws IOException {
        when(node.getFillColor()).thenReturn(Color.WHITE);
		render();
        verify(context).fill(new Rectangle(0f, 12.5f, 20f, 20f), Color.WHITE);
    }

    @Test
    public void testRendering() throws IOException {
        render("text");
        verify(context).drawText("text", null, 0, 15);
    }

    @Test
    public void ifCellisNotMerged_nothingIsDone() throws IOException {
        when(node.isMerged(context)).thenReturn(false);
        render();
        verify(context, never()).drawText(anyString(),any(Color.class), anyFloat(), anyFloat());
        verify(context, never()).fill(any(Rectangle.class), any(Color.class));
    }

    @Override
    protected Renderer createRenderer() {
        return new MergedCellRenderer(node);
    }

    @Before
    public void setup() {
        init();
        MergedRegion mergedRegion = new MergedRegion("A1:B2");

        when(node.getParent()).thenReturn(nodeParent);
        when(node.isMerged(context)).thenReturn(true);
        when(nodeParent.getHeigth(eq(renderingContext), any(Range.class))).thenReturn(10f);
        when(nodeParent.getMergedRegionWidth(eq(renderingContext), eq(node), any(MergedRegion.class))).thenReturn(20f);
        when(node.getMergedRegion(context)).thenReturn(mergedRegion);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row = sheet.createRow(1);
        row.createCell(1);
        when(context.getSheet()).thenReturn(sheet);
        when(context.getY()).thenReturn(25f);
    }

	@Override
	protected Set<RenderingParameter> getDefaultParameters() {
		Set<RenderingParameter> params = new HashSet<RenderingParameter>();
		params.add(new RenderingParameter(RenderingParameter.ROW_HEIGHT, 5f));
		return params;
	}
}
