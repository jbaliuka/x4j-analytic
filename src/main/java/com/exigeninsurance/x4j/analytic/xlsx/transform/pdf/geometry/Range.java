/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.geometry;


public interface Range {
	
	boolean inside(int number);
	
	int getFirst();
	
	int getLast();
	
	void setFirst(int number);
	
	void setLast(int number);
	
	Range copy();

	boolean contains(Range range);

	int length();

}
