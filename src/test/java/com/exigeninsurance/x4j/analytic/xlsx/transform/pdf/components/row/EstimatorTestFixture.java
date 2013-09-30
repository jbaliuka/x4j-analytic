/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;

public class EstimatorTestFixture {
    @Mock protected PdfCellNode child1;
    @Mock protected PdfCellNode child2;
    @Mock protected PdfContainer row;
    @Mock protected PdfContext context;

    protected List<Node> children;
    protected Estimator estimator;

	protected RenderingContext renderingContext;

    public void setup() {
        initMocks(this);
        children = new ArrayList<Node>();
        children.add(child1);
        children.add(child2);
        when(row.getChildren()).thenReturn(children);

		renderingContext = new RenderingContext(context, RenderingParameters.empty());
    }
}
