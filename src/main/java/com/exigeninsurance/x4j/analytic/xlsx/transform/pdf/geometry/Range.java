/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;


public interface Range {
	
	public boolean inside(int number);
	
	public int getFirst();
	
	public int getLast();
	
	public void setFirst(int number);
	
	public void setLast(int number);
	
	public Range copy();

	public boolean contains(Range range);

	public int length();

}
