/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;

public class DefaultRowProcessorTest {

    private Processor processor;

    private List<Node> children;

    @Mock private PdfContainer row;
    @Mock private PdfContext context;

    @Mock private Node child1;
    @Mock private Node child2;

    @Before
    public void testProcess() {
        initMocks(this);
        processor = new DefaultRowProcessor(row);
        when(row.estimateHeight(any(RenderingContext.class))).thenReturn(10f);
        children = new ArrayList<Node>();
        children.add(child1);
        children.add(child2);
    }

    @Test
    public void testSelfRendering() throws Exception {
        InOrder inOrder = inOrder(context);
        processor.process(context);
        inOrder.verify(context).prepareNewLine(10 + PdfRenderer.ROW_MARGIN);
        inOrder.verify(context).drawLater(eq(row), any(RenderingParameters.class));
    }

    @Test
    public void testChildrenProcessing() throws Exception {
        when(row.getChildren()).thenReturn(children);
        processor.process(context);
        verify(child1).process(context);
        verify(child2).process(context);
    }
}
