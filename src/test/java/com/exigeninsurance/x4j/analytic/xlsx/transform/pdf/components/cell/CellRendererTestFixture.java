/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Alignment;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;

public abstract class CellRendererTestFixture {

    protected static final String ONE_ROW = "one row";
    protected static final String TWO_ROWS = "first second";
    @Mock protected PdfCellNode node;
    @Mock protected XSSFCellStyle cellStyle;
    @Mock protected PdfContext context;
    protected Renderer renderer;
	protected RenderingContext renderingContext;
	protected RenderingParameters renderingParameters;

    public void init() {
		initMocks(this);
		renderingParameters = RenderingParameters.empty();
		renderingContext = new RenderingContext(context, renderingParameters);

		initCellStyle();
		initNode();
		renderer = createRenderer();
    }

    protected void initNode() {
        when(node.getCellStyle()).thenReturn(cellStyle);
        when(node.getHorizontalAlignment()).thenReturn(Alignment.LEFT);
        when(node.getMaxFontHeight()).thenReturn(10f);
        when(node.getVerticalAlignment()).thenReturn(CellStyle.VERTICAL_BOTTOM);
        when(node.estimateHeight(renderingContext)).thenReturn(25f);
        when(node.estimateWidth(renderingContext)).thenReturn(25f);

        when(node.formatValue(eq(context), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                return value == null ? "" : value;
            }
        });
    }

    protected void initCellStyle() {
        when(cellStyle.getBorderBottomEnum()).thenReturn(BorderStyle.NONE);
        when(cellStyle.getBorderTopEnum()).thenReturn(BorderStyle.NONE);
        when(cellStyle.getBorderLeftEnum()).thenReturn(BorderStyle.NONE);
        when(cellStyle.getBorderRightEnum()).thenReturn(BorderStyle.NONE);
    }

    protected abstract Renderer createRenderer();

	protected void render() throws IOException {
		renderingContext.setParams(new RenderingParameters(getDefaultParameters()));
		renderer.render(renderingContext);
	}

	protected void render(String cellValue) throws IOException {
		Set<RenderingParameter> params = getDefaultParameters();
		params.add(new RenderingParameter(RenderingParameter.CELL_VALUE, cellValue));
		renderingParameters = new RenderingParameters(params);
		renderingContext.setParams(renderingParameters);
		renderer.render(renderingContext);
	}

	protected Set<RenderingParameter> getDefaultParameters() {
		return new HashSet<RenderingParameter>();
	}
}
