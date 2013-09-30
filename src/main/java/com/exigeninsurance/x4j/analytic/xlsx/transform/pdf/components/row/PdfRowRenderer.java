/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import java.io.IOException;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Renderer;


public abstract class PdfRowRenderer implements Renderer {
	
protected final PdfContainer rowNode;
	
	public PdfRowRenderer(PdfContainer rowNode) {
		this.rowNode = rowNode;
	}

	public abstract void render(RenderingContext context) throws IOException;

}
