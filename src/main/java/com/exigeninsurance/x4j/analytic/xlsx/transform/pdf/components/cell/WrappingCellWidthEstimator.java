/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;


public class WrappingCellWidthEstimator extends PdfCellEstimator {
	private Estimator estimator;

	public WrappingCellWidthEstimator(PdfCellNode node, Estimator estimator) {
		super(node);
		this.estimator = estimator;
	}

	@Override
	public float estimate(RenderingContext context) {
		PdfContext pdfContext = context.getPdfContext();
		String value = formatValue(context);
		List<String> rows = node.splitCell(value, estimator.estimate(context), pdfContext.getMargins());

		return findLargestRow(rows);
	}

	private float findLargestRow(List<String> rows) {
		float max = 0f;
		for (String row : rows) {
			max = Math.max(max, node.findTextLength(row));
		}
		return max;
	}
}
