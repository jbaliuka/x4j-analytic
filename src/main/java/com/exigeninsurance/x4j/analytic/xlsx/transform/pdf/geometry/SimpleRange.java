/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;


public class SimpleRange implements Range {
	
	private int start;
	private int end;
	
	public SimpleRange() {
		
	}
	
	public SimpleRange(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public boolean inside(int number) {
		return number >= start && number <= end;
	}
	
	public Range copy() {
		return new SimpleRange(start, end);
	}

	@Override
	public int getFirst() {
		return start;
	}

	@Override
	public int getLast() {
		return end;
	}

	@Override
	public void setFirst(int number) {
        start = number;
	}

	@Override
	public void setLast(int number) {
        end = number;
	}

	@Override
	public boolean contains(Range range) {
		return start <= range.getFirst() && end >= range.getLast();
	}

	@Override
	public int length() {
		return end - start + 1;
	}
}
