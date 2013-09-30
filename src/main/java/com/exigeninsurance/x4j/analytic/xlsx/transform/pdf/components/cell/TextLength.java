/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class TextLength extends PdfCellEstimator {
    public TextLength(PdfCellNode node) {
        super(node);
    }

    @Override
    public float estimate(RenderingContext context) {
		PdfContext pdfContext = context.getPdfContext();
		return node.findTextLength(node.formatValue(pdfContext, evaluate(pdfContext)));
    }

    private Object evaluate(PdfContext context) {
        try {
            return node.getExpression().evaluate(context);
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }
}
