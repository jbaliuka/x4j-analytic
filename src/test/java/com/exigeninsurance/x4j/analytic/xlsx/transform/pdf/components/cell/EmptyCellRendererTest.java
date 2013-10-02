/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;


import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;

public class EmptyCellRendererTest {

    @Mock PdfCellNode cellNode;
    @Mock PdfContext context;

    EmptyCellRenderer renderer;

    @Before
    public void setup() {
        initMocks(this);
        renderer = new EmptyCellRenderer(cellNode);
    }

    @Test
    public void verifyPointerWasMoved() throws IOException {
		RenderingContext renderingContext = new RenderingContext(context, new RenderingParameters(RenderingParameter.ROW_HEIGHT, 10f));
		when(cellNode.estimateWidth(renderingContext)).thenReturn(10f);
		renderer.render(renderingContext);
        verify(context).movePointerBy(eq(10f), eq(0f));
    }
}
