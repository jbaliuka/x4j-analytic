/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;



public final class XLSXErrorValue {
	public static final XLSXErrorValue INSATNCE = new XLSXErrorValue();
	
	private XLSXErrorValue(){
		
	}
	@Override
	public String toString() {
		return "#VALUE!";
	}
}
