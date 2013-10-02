/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Constant;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;

public class MarginsTest {

    @Mock private PdfContext context;
    private Estimator estimator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Estimator wrappedEstimator = new Constant(5);
        estimator = new Margins(wrappedEstimator);
        when(context.getMargins()).thenReturn(5f);
    }

    @Test
    public void estimateReturnsOriginalPlusMargins() {
        assertThat(estimator.estimate(new RenderingContext(context, RenderingParameters.empty())), equalTo(10f));
    }
}
