/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;


public class TableStyle {
	
	private String name;
	private boolean pivot;
	
	private PdfStyle wholeTableStyle;
	private PdfStyle headerRowStyle;
	private PdfStyle totalRowStyle;
	private PdfStyle firstColumnStyle;
	private PdfStyle lastColumnStyle;
	private PdfStyle firstRowStripeStyle;
	private PdfStyle firstColumnStripeStyle;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isPivot() {
		return pivot;
	}
	
	public void setPivot(boolean pivot) {
		this.pivot = pivot;
	}
	
	public PdfStyle getWholeTableStyle() {
		return wholeTableStyle;
	}
	
	public void setWholeTableStyle(PdfStyle wholeTableStyle) {
		this.wholeTableStyle = wholeTableStyle;
	}
	
	public PdfStyle getHeaderRowStyle() {
		return headerRowStyle;
	}
	
	public void setHeaderRowStyle(PdfStyle headerRowStyle) {
		this.headerRowStyle = headerRowStyle;
	}
	
	public PdfStyle getTotalRowStyle() {
		return totalRowStyle;
	}
	
	public void setTotalRowStyle(PdfStyle totalRowStyle) {
		this.totalRowStyle = totalRowStyle;
	}
	
	public PdfStyle getFirstColumnStyle() {
		return firstColumnStyle;
	}
	
	public void setFirstColumnStyle(PdfStyle firstColumnStyle) {
		this.firstColumnStyle = firstColumnStyle;
	}
	
	public PdfStyle getLastColumnStyle() {
		return lastColumnStyle;
	}
	
	public void setLastColumnStyle(PdfStyle lastColumnStyle) {
		this.lastColumnStyle = lastColumnStyle;
	}
	
	public PdfStyle getFirstRowStripeStyle() {
		return firstRowStripeStyle;
	}
	
	public void setFirstRowStripeStyle(PdfStyle firstRowStripeStyle) {
		this.firstRowStripeStyle = firstRowStripeStyle;
	}
	
	public PdfStyle getFirstColumnStripeStyle() {
		return firstColumnStripeStyle;
	}
	
	public void setFirstColumnStripeStyle(PdfStyle firstColumnStripeStyle) {
		this.firstColumnStripeStyle = firstColumnStripeStyle;
	}
}
