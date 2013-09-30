/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;


public abstract class PdfCellEstimator implements Estimator {
	
	protected final PdfCellNode node;
	
	public PdfCellEstimator(PdfCellNode node) {
		this.node = node;
	}

	protected String formatValue(RenderingContext context) {
		if (context.getParams().has(RenderingParameter.CELL_VALUE)) {
			return node.formatValue(context.getPdfContext(), context.getParams().get(RenderingParameter.CELL_VALUE));
		}

		return evaluateCurrent(context);
	}

	private String evaluateCurrent(RenderingContext context) {
		String s;
		try {
			s =  node.formatValue(context.getPdfContext(), node.getExpression().evaluate(context.getPdfContext()));
		} catch (Exception e) {
			throw new ReportException(e);
		}
		return s;
	}

}
