/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class BasicCellHeigthEstimator extends PdfCellEstimator {

	public BasicCellHeigthEstimator(PdfCellNode node) {
		super(node);
	}

	@Override
	public float estimate(RenderingContext context) {
		return node.getMaxFontHeight();
	}

}
