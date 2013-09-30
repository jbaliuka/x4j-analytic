/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class HorizontalContainerHeigthEstimator extends PdfContainerEstimator {

	public HorizontalContainerHeigthEstimator(PdfContainer rowNode) {
		super(rowNode);
	}

	@Override
	public float doEstimate(RenderingContext context) {
		float max = 0f;
		for (Node cell : rowNode.getChildren()) {
			float current = ((DrawablePdfElement) cell).estimateHeight(context);
			if (current > max) {
				max = current;
			}
		}
		return max;
	}

}
