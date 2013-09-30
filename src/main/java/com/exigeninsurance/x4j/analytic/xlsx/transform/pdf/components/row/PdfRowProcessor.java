/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public abstract class PdfRowProcessor implements Processor {
	
	protected final PdfContainer rowNode;
	
	public PdfRowProcessor(PdfContainer node) {
        rowNode = node;
	}

	public abstract void process(XLXContext context) throws Exception;

}
