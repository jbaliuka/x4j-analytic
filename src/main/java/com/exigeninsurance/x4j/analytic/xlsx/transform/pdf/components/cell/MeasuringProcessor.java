
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class MeasuringProcessor extends PdfCellProcessor{

    private Estimator widthEstimator;

    public MeasuringProcessor(PdfCellNode node, Estimator widthEstimator) {
        super(node);
        this.widthEstimator = widthEstimator;
    }

    @Override
    public void process(XLXContext context) throws Exception {
        PdfContext pdfContext = (PdfContext) context;
        pdfContext.reportColumnWidth(node.getColRef(), widthEstimator.estimate(new RenderingContext(pdfContext, RenderingParameters.empty())));
    }
}
