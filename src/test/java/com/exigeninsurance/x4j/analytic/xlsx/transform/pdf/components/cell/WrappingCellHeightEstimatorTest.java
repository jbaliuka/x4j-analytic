
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;

public class WrappingCellHeightEstimatorTest {

    @Mock private PdfCellNode node;
    @Mock private PdfContext context;
    @Mock private XLSXExpression expression;
    private WrappingCellHeigthEstimator estimator;
	private RenderingContext renderingContext;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

		renderingContext = new RenderingContext(context, RenderingParameters.empty());
        estimator = new WrappingCellHeigthEstimator(node);
        when(node.getMaxFontHeight()).thenReturn(10f);
        when(node.getExpression()).thenReturn(expression);
        when(expression.evaluate(context)).thenReturn("Test String");
    }

    @Test
    public void empty() {
        when(node.splitCell(anyString(), anyFloat(), anyFloat())).thenReturn(Collections.<String>emptyList());
        assertThat(estimator.estimate(renderingContext), equalTo(0f));
    }

    @Test
    public void oneLine() {
        when(node.splitCell(anyString(), anyFloat(), anyFloat())).thenReturn(new ArrayList<String>(Arrays.asList("1")));
        assertThat(estimator.estimate(renderingContext), equalTo(10f));
    }

    @Test
    public void twoLines() {
        when(node.splitCell(anyString(), anyFloat(), anyFloat())).thenReturn(new ArrayList<String>(Arrays.asList("1", "2")));
        assertThat(estimator.estimate(renderingContext), equalTo(25f));
    }

    @Test
    public void threeLines() {
        when(node.splitCell(anyString(), anyFloat(), anyFloat())).thenReturn(new ArrayList<String>(Arrays.asList("1", "2", "3")));
        assertThat(estimator.estimate(renderingContext), equalTo(40f));
    }
}
