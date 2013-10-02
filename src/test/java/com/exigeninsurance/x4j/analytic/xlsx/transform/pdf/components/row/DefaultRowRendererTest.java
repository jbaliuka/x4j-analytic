
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;

public class DefaultRowRendererTest {

    @Mock private PdfContainer row;
    @Mock private PdfContext context;

    private Renderer renderer;
	private RenderingContext renderingContext;

	@Before
    public void setup() {
        initMocks(this);
        renderer = new DefaultRowRenderer(row);
		renderingContext = new RenderingContext(context, RenderingParameters.empty());
        when(row.estimateHeight(renderingContext)).thenReturn(10f);
    }

    @Test
    public void testRender() throws Exception {
		renderer.render(renderingContext);
        verify(context).movePointerToNewLine(10 + PdfRenderer.ROW_MARGIN);
    }
}
