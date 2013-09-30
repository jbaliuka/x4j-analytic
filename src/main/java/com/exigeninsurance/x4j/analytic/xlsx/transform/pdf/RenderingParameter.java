/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;



public class RenderingParameter {

	public static final String CELL_VALUE = "cellValue";
	public static final String PICTURE_DATA = "pictureData";
	public static final String ROW_HEIGHT = "rowHeight";

	private String name;
	private Object value;

	public RenderingParameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
