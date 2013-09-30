/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;



public class RenderingContext {

	private PdfContext context;
	private RenderingParameters params;

	public RenderingContext(PdfContext context, RenderingParameters params) {
		this.context = context;
		this.params = params;
	}

	public PdfContext getPdfContext() {
		return context;
	}

	public RenderingParameters getParams() {
		return params;
	}

	public void setParams(RenderingParameters params) {
		this.params = params;
	}
}
