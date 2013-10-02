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

public class HorizontalContainerHeigthEstimatorTest extends EstimatorTestFixture {
    @Before
    public void setup() {
        super.setup();
        estimator = new HorizontalContainerHeigthEstimator(row);
    }

    @Test
    public void testEstimateHeight() {
        when(child1.estimateHeight(renderingContext)).thenReturn(1f);
        when(child2.estimateHeight(renderingContext)).thenReturn(2f);
        float height = estimator.estimate(renderingContext);
        assertThat(height, equalTo(2f));
    }
}
