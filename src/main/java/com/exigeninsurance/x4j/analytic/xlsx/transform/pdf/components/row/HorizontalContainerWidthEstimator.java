/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.DrawablePdfElement;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class HorizontalContainerWidthEstimator extends PdfContainerEstimator {

	public HorizontalContainerWidthEstimator(PdfContainer rowNode) {
		super(rowNode);
	}

	public float doEstimate(RenderingContext context) {
		float width = 0f;
        for (Node cell : rowNode.getChildren()) {
			width += ((DrawablePdfElement) cell).estimateWidth(context);
		}
		return width;
	}

}
