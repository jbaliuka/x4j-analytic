/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;


public abstract class PdfContainerEstimator implements Estimator {
	
	protected final PdfContainer rowNode;
	
	public PdfContainerEstimator(PdfContainer rowNode) {
		this.rowNode = rowNode;
	}

	public float estimate(RenderingContext context) {
		if (context.getParams().has(RenderingParameter.ROW_HEIGHT)) {
			return (Float) context.getParams().get(RenderingParameter.ROW_HEIGHT);
		}
		return doEstimate(context);
	}

	public abstract float doEstimate(RenderingContext context);
}
