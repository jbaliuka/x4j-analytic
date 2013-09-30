/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;


public class CurrentPageInstruction extends TextInstruction {

	public CurrentPageInstruction(String contents) {
		super(contents);
	}

	@Override
	public String evaluateContents(PdfContext context) {
		return Integer.toString(context.getCurrentPageNumber());
	}

}
