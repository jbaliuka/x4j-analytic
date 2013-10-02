/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;

public class TableRowProcessorTest {

    @Mock private PdfContext context;
    @Mock private PdfContainer row;
    @Mock private TableStyle tableStyle;

    @Mock private PdfCellNode child1;
    @Mock private PdfCellNode child2;

    private Processor processor;

	@Before
    public void setup() {
        initMocks(this);
        processor = new TableRowProcessor(row);
		List<Node> children = new ArrayList<Node>();
        children.add(child1);
        children.add(child2);
        when(row.getChildren()).thenReturn(children);
        when(context.findTableStyle(anyInt())).thenReturn(tableStyle);
        when(child1.getColor(tableStyle)).thenReturn(Color.WHITE);
        when(child2.getColor(tableStyle)).thenReturn(Color.WHITE);
    }


    @Test
    public void testSelfRendering() throws Exception {
        when(row.estimateHeight(any(RenderingContext.class))).thenReturn(10f);
        InOrder inOrder = inOrder(context);
        processor.process(context);
        inOrder.verify(context).prepareNewLine(10f + PdfRenderer.ROW_MARGIN);
        inOrder.verify(context).drawLater(eq(row), any(RenderingParameters.class));
    }

    @Test
    public void testProcess() throws Exception {
        processor.process(context);
        verify(row).setTableId(0);
    }
}
