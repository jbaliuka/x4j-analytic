/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.rule.HeaderFooterApplicabilityRule;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class PdfFooter extends PdfHeaderFooter {
	
	public PdfFooter(String contents, XLXContext context,
			HeaderFooterApplicabilityRule applicabilityRule) throws Exception {
		super(contents, context, applicabilityRule);
	}

	@Override
	public void draw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
		for (DrawablePdfElement section : getSections()) {
			pdfContext.movePointerTo(pdfContext.getLeftHorizontalMargin(), estimateHeight(context) + pdfContext.getFooterMargin());
			section.draw(context);
		}
	}

    @Override
    protected String getCode() {
        return "F";
    }
}
