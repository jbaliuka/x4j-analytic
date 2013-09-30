/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class VerticalContainerWidthEstimator extends PdfContainerEstimator {

	public VerticalContainerWidthEstimator(PdfContainer rowNode) {
		super(rowNode);
	}

	@Override
	public float doEstimate(RenderingContext context) {
		float maxWidth = 0f;
		List<Node> children = rowNode.getChildren();
		for (Node child : children) {
			float width = ((DrawablePdfElement) child).estimateWidth(context);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

}
