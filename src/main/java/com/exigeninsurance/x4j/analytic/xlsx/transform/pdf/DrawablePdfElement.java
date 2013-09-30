/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;



public interface DrawablePdfElement {
	
	public void draw(RenderingContext renderingContext) throws Exception;
		
	public float estimateWidth(RenderingContext context);
	
	public float estimateHeight(RenderingContext context);
	
	public void notify(PdfContext context);
	
	public void setParent(PdfContainer element);
	
	public PdfContainer getParent();
	
	public float getHeigth();
}
