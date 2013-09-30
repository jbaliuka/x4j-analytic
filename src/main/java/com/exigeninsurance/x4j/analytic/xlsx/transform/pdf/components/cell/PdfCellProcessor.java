/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public abstract class PdfCellProcessor implements Processor {
	
	protected final PdfCellNode node;

	public PdfCellProcessor(PdfCellNode node) {
		super();
		this.node = node;
	}

	public abstract void process(XLXContext context) throws Exception;

}
