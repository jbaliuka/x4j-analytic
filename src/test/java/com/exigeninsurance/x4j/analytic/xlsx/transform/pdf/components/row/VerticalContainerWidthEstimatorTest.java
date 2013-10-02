/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class VerticalContainerWidthEstimatorTest extends EstimatorTestFixture {

    @Before
    public void setup() {
        super.setup();
        estimator = new VerticalContainerWidthEstimator(row);
    }

    @Test
    public void testEstimate() {
        when(child1.estimateWidth(renderingContext)).thenReturn(1f);
        when(child2.estimateWidth(renderingContext)).thenReturn(2f);
        float height = estimator.estimate(renderingContext);
        assertThat(height, equalTo(2f));
    }
}
