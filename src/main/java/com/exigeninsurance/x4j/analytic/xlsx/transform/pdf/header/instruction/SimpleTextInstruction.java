/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;


public class SimpleTextInstruction extends TextInstruction {
	
	public SimpleTextInstruction(String contents) {
		super(contents);
	}

	@Override
	public String evaluateContents(PdfContext context) {
		return contents;
	}
}
