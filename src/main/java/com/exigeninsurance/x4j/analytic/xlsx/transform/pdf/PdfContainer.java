/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Estimator;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Notifier;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;



public class PdfContainer extends Node implements DrawablePdfElement, PdfGridElement {
	
	private PdfContainer parent;
	
	private Estimator heigthEstimator;
	private Estimator widthEstimator;
	private Processor processor;
	private Renderer renderer;
	private Notifier notifier;
	
	private float heigth;
	private int tableId;
	private Range verticalRange;

	public PdfContainer(XSSFSheet sheet, Range verticalRange) {
		super(sheet);
		this.verticalRange = verticalRange;
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
		processor.process(context);
	}

	public void draw(RenderingContext context) throws Exception {
		renderer.render(context);
	}

	public float estimateWidth(RenderingContext context)  {
		return widthEstimator.estimate(context);
	}

	public float estimateHeight(RenderingContext context) {
		heigth = heigthEstimator.estimate(context);
		return heigth;
	}

	public void notify(PdfContext context) {
		notifier.notify(context);
	}
	
	public float getHeigth(RenderingContext context, Range range) {
		if (verticalRange.contains(range)) {
			return calculateHeigth(context, range);
		}
		else {
			return extractHeigthFromParent(context, range);
		}
	}
	
	public float getMergedRegionWidth(RenderingContext context, PdfCellNode regionHead, MergedRegion region) {
		int start = getChildren().indexOf(regionHead);
		if (start != -1) {
			int end = start + region.getHorizontalRange().length() - 1;
			return extractWidth(context, start, end);
		}
		throw new IllegalStateException("Incorrectly transformed layout");
	}

	private float extractWidth(RenderingContext context, int from, int to) {
		float totalWidth = 0f;
		for (int i = from; i <= to; i++) {
			DrawablePdfElement child = (DrawablePdfElement) getChildren().get(i);
			totalWidth += child.estimateWidth(context);
		}
		
		PdfContext ctx = context.getPdfContext();
		return Math.min(totalWidth,ctx.getPageWidth() - ctx.getLeftHorizontalMargin() - ctx.getRightHorizontalMargin());
	}

	private float extractHeigthFromParent(RenderingContext context, Range range) {
		if (parent == null) {
			throw new IllegalStateException("Incorrectly transformed layout");
		}
		return parent.getHeigth(context, range);
	}

	private float calculateHeigth(RenderingContext context, Range range) {
		float totalHeigth = 0f;
		int startIndex = findStart(range.getFirst());
		int endIndex = startIndex + range.length();
		for (int i = startIndex; i < endIndex; i++) {
			DrawablePdfElement child = (DrawablePdfElement) getChildren().get(i);
			totalHeigth += child.estimateHeight(context);
		}
		return totalHeigth;
	}

	private int findStart(int start) {
		List<Node> children = getChildren();
		int i = 0;
		for (Node child : children) {
			int first = ((PdfGridElement) child).getVerticalRange().getFirst();
			if (first == start) {
				return i;
			}
			i++;
		}
		throw new IllegalStateException();
	}

	public Estimator getHeigthEstimator() {
		return heigthEstimator;
	}

	public void setHeigthEstimator(Estimator heigthEstimator) {
		this.heigthEstimator = heigthEstimator;
	}

	public Estimator getWidthEstimator() {
		return widthEstimator;
	}

	public void setWidthEstimator(Estimator widthEstimator) {
		this.widthEstimator = widthEstimator;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public Notifier getNotifier() {
		return notifier;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	public Range getVerticalRange() {
		return verticalRange;
	}

	public void setVerticalRange(Range verticalRange) {
		this.verticalRange = verticalRange;
	}

	@Override
	public void setParent(PdfContainer element) {
		parent = element;
		
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return heigth;
	}
}
