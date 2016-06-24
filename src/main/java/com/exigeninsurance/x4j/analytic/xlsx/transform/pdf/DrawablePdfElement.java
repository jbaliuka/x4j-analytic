/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;



public interface DrawablePdfElement {
	
	void draw(RenderingContext renderingContext) throws Exception;
		
	float estimateWidth(RenderingContext context);
	
	float estimateHeight(RenderingContext context);
	
	void notify(PdfContext context);
	
	void setParent(PdfContainer element);
	
	PdfContainer getParent();
	
	float getHeigth();
}
