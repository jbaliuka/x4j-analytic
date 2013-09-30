/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;


public class Margins implements Estimator{
    private final Estimator wrappedEstimator;

    public Margins(Estimator wrappedEstimator) {
        this.wrappedEstimator = wrappedEstimator;
    }

    @Override
    public float estimate(RenderingContext context) {
        return wrappedEstimator.estimate(context) + context.getPdfContext().getMargins();
    }
}
