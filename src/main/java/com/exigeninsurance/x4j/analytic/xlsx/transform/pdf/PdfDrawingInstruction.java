/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;


public class PdfDrawingInstruction {
	
	private DrawablePdfElement drawable;
	private RenderingParameters params;

	public PdfDrawingInstruction(DrawablePdfElement drawable, RenderingParameters params) {
		this.drawable = drawable;
		this.params = params;
	}
	
	public DrawablePdfElement getDrawable() {
		return drawable;
	}
	
	public void draw(PdfContext context) throws Exception {
		drawable.draw(new RenderingContext(context, params));
	}
}
