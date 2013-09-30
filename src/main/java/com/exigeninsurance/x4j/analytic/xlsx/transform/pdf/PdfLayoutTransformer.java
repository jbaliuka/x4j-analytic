/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;


public class PdfLayoutTransformer {
	
	private final PdfContext context;
	private Collection<MergedRegion> mergedRegions;
	
	private boolean currentlyWrapping;
	private PdfContainer wrapper;
	private Range currentRange;
    private ComponentFactory componentFactory;
	
	public PdfLayoutTransformer(PdfContext context, ComponentFactory componentFactory) {
		this.context = context;
        this.componentFactory = componentFactory;
	}

	public void transform(Node root) {
		groupRowsWithMergedRegions(root);
	}
	
	private void groupRowsWithMergedRegions(Node root) {
		prepareForGrouping();
		group(root);
	}
	
	private void group(Node node) {
		List<Node> transformedChildren = new ArrayList<Node>();
		for (Node child : node.getChildren()) {
			processNode(child, transformedChildren);
		}
		node.setChildren(transformedChildren);
	}
	
	private void processNode(Node node, List<Node> transformedChildren) {
		if (needsWrapping(node)) {
			processWrappableNode(node, transformedChildren);
		}
		else {
			group(node);
			transformedChildren.add(node);
		}
	}

	private boolean needsWrapping(Node node) {
		return node instanceof PdfContainer;
	}

	private void processWrappableNode(Node node, List<Node> transformedChildren) {
		PdfContainer row = (PdfContainer)node;
		List<MergedRegion> startingMergedRegions = findStartingMergedRegions(row);
		if (!startingMergedRegions.isEmpty()) {
			createWrapper(startingMergedRegions, row);
		}
		
		wrapIfNeeded(row, transformedChildren);
		
		if (isEndOfMergedRegion(row)) {
			saveWrapper(transformedChildren);
		}
	}
	
	private void prepareForGrouping() {
		currentlyWrapping = false;
		mergedRegions = context.getMergedCells().values();
	}

	private List<MergedRegion> findStartingMergedRegions(PdfContainer rowNode) {
		List<MergedRegion>regions = new ArrayList<MergedRegion>();
		int first = rowNode.getVerticalRange().getFirst();
		for (MergedRegion region : mergedRegions) {
			if (region.isFirstRow(first + 1)) {
				regions.add(region);
			}
		}
		return regions;
	}

	private void wrapIfNeeded(Node row, List<Node> newChildren) {
		if (currentlyWrapping) {
			wrap(row);
		}
		else {
			newChildren.add(row);
		}
	}
	
	private boolean isEndOfMergedRegion(PdfContainer rowNode) {
		if (!currentlyWrapping) {
			return false;
		}
		return currentRange.getLast() == rowNode.getVerticalRange().getLast();
	}
	
	private void wrap(Node row) {
		wrapper.getChildren().add(row);
		((DrawablePdfElement)row).setParent(wrapper);
	}

	private void createWrapper(List<MergedRegion> startingMergedRegions, PdfContainer row) {
		int lastRow = findLastRow(startingMergedRegions);
		if (currentlyWrapping) {
			extendCurrentRangeIfNeeded(lastRow);
		}
		else {
			enterWrappingState(row.getVerticalRange().getFirst(), lastRow);
		}
	}
	
	private void enterWrappingState(int firstRow, int lastRow) {
		currentRange = new SimpleRange(firstRow, lastRow);
		wrapper = componentFactory.createVerticalRowWrapper(context.getSheet(), currentRange);
		currentlyWrapping = true;
	}
	
	private void extendCurrentRangeIfNeeded(int lastRow) {
		if (lastRow > currentRange.getLast()) {
			currentRange.setLast(lastRow);
		}
	}
	
	private int findLastRow(List<MergedRegion> regions) {
		int max = 0;
		for (MergedRegion region : regions) {
			int heigth = region.getLastRow() - 1;
			if (heigth > max) {
				max = heigth;
			}
		}
		return max;
	}
	
	private void saveWrapper(List<Node> newChildren) {
		currentlyWrapping = false;
		wrapper.setVerticalRange(currentRange.copy());
		newChildren.add(wrapper);
	}
}
