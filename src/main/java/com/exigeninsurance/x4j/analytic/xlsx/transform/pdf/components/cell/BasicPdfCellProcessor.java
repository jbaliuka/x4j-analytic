/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class BasicPdfCellProcessor extends PdfCellProcessor {

	public BasicPdfCellProcessor(PdfCellNode node) {
		super(node);
	}

	public void process(XLXContext context) {
		PdfContext pdfContext = (PdfContext) context;
		Object value;
		try {
			value = node.getExpression().evaluate(context);
		} catch (Exception e) {
			throw new ReportException(e);
		}
		pdfContext.drawLater(node, createRenderingParams(value));
	}

	private RenderingParameters createRenderingParams(Object value) {
		return new RenderingParameters(
				new RenderingParameter(RenderingParameter.CELL_VALUE, value),
				new RenderingParameter(RenderingParameter.ROW_HEIGHT, node.getRowHeight()));
	}

}
