/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class VerticalContainerHeigthEstimator extends PdfContainerEstimator {

	public VerticalContainerHeigthEstimator(PdfContainer rowNode) {
		super(rowNode);
	}

	@Override
	public float doEstimate(RenderingContext context) {
		float height = 0f;
        for (Node child : rowNode.getChildren()) {
			height += ((DrawablePdfElement) child).estimateHeight(context);
		}
		return height;
	}

}
