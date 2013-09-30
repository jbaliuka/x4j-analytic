/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import java.io.IOException;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class DefaultRowRenderer extends PdfRowRenderer {

	public DefaultRowRenderer(PdfContainer rowNode) {
		super(rowNode);
	}

	@Override
	public void render(RenderingContext context) throws IOException {
		PdfContext pdfContext = context.getPdfContext();
		pdfContext.movePointerToNewLine(rowNode.estimateHeight(context) + PdfRenderer.ROW_MARGIN);
	}

}
