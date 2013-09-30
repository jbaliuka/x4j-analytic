/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;


public class ExpandedColumn extends PdfCellEstimator {
    private final Estimator wrappedEstimator;

    public ExpandedColumn(PdfCellNode node, Estimator wrappedEstimator) {
        super(node);
        this.wrappedEstimator = wrappedEstimator;
    }

    @Override
    public float estimate(RenderingContext context) {
		PdfContext pdfContext = context.getPdfContext();

		String col = node.getColRef();
		float defaultEstimate = wrappedEstimator.estimate(context);
		return pdfContext.isExpanded(col) ?
                Math.max(pdfContext.getColumnWidth(col), defaultEstimate) : defaultEstimate;
    }
}
