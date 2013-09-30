/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class VerticalContainerProcessor extends PdfRowProcessor {

	public VerticalContainerProcessor(PdfContainer node) {
		super(node);
	}

	@Override
	public void process(XLXContext context) throws Exception {
		scheduleRowNodeForDrawing((PdfContext) context);
		processRowNodeChildren(context);

	}

	private void processRowNodeChildren(XLXContext context) throws Exception {
		for (Node node : rowNode.getChildren()) {
			node.process(context);
		}
	}

	private void scheduleRowNodeForDrawing(PdfContext context) throws Exception {
		float height = rowNode.estimateHeight(new RenderingContext(context, RenderingParameters.empty()));
		context.ensureEnoughSpace(height + PdfRenderer.ROW_MARGIN);

		RenderingParameters params = new RenderingParameters(RenderingParameter.ROW_HEIGHT, height);
		context.drawLater(rowNode, params);
	}
}
