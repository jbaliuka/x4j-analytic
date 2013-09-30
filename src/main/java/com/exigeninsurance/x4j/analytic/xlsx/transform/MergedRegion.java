/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import org.apache.poi.ss.util.CellReference;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.Range;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry.SimpleRange;


public class MergedRegion {

	private String topLeftCol;
	private int topLeftRow;
	private String bottomRightCol;
	private int bottomRightRow;
	private int rowHeigthDifference;
	private Range horizontalRange;
	private Range verticalRange;
    private String topLeftRef;
    private String bottomRightRef;

    public MergedRegion(String region) {
		String[] refs = region.split(":");
		assert(refs.length == 2);
        topLeftRef = refs[0];
        bottomRightRef = refs[1];
		parseTopLeft(topLeftRef);
		parseBottomRight(bottomRightRef);
		rowHeigthDifference = bottomRightRow - topLeftRow;
		verticalRange = new SimpleRange(topLeftRow - 1, bottomRightRow - 1);
		int topRef = CellReference.convertColStringToIndex(topLeftCol);
		int bottomRef = CellReference.convertColStringToIndex(bottomRightCol);
		horizontalRange = new SimpleRange(topRef, bottomRef);
	}
	
	
	
	private MergedRegion(String topLeftCol, int topLeftRow,
			String bottomRightCol, int bottomRightRow) {
		this.topLeftCol = topLeftCol;
		this.topLeftRow = topLeftRow;
		this.bottomRightCol = bottomRightCol;
		this.bottomRightRow = bottomRightRow;
		rowHeigthDifference = bottomRightRow - topLeftRow;
	}
	
	/**
	 * row index should be 1-based
	 */
	
	public boolean isFirstRow(int row) {
		return topLeftRow == row;
	}
	
	/**
	 * row index should be 1-based
	 */
	
	public boolean isLastRow(int row) {
		return bottomRightRow == row;
	}
	
	public int getRegionHeigth() {
		return rowHeigthDifference + 1;
	}
	
	public int getLastRow() {
		return bottomRightRow;
	}

    public String getTopLeftRef() {
        return topLeftRef;
    }

    public String getBottomRightRef() {
        return bottomRightRef;
    }

    private void parseBottomRight(String bottomRightRef) {
		bottomRightCol = parseCol(bottomRightRef);
		bottomRightRow = Integer.parseInt(bottomRightRef.substring(bottomRightCol.length()));
	}

	private void parseTopLeft(String topLeftRef) {
		topLeftCol = parseCol(topLeftRef);
		topLeftRow = Integer.parseInt(topLeftRef.substring(topLeftCol.length()));
	}

	@Override
	public String toString() {
		return topLeftCol + topLeftRow + ":" + bottomRightCol + bottomRightRow;
	}
	
	public MergedRegion derive(int row) {
		return new MergedRegion(topLeftCol, row, bottomRightCol, rowHeigthDifference + row);
	}
	
	public Range getVerticalRange() {
		return verticalRange;
	}
	
	public Range getHorizontalRange() {
		return horizontalRange;
	}
	
	private String parseCol(String ref) {
		StringBuilder builder = new StringBuilder();
		for (char character : ref.toCharArray()) {
			if (Character.isDigit(character)) {
				break;
			}
			builder.append(character);
		}
		return builder.toString();
	}
	
	/**
	 * 
	 * params are 0-based
	 */
	
	public boolean isInRegion(int row, int col) {
		Range vertical = getVerticalRange();
		Range horizontal = getHorizontalRange();
		return vertical.inside(row) && horizontal.inside(col);
	}
}
