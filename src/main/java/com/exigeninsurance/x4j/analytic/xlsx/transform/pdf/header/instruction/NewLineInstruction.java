/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class NewLineInstruction extends Instruction {

	public NewLineInstruction(String contents) {
		super(contents);
	}

	public void draw(RenderingContext context) throws Exception {
		
	}

	public float estimateWidth(RenderingContext context) {
		return 0;
	}

	public float estimateHeight(RenderingContext context) {
		return 0;
	}

	public void notify(PdfContext context) {
		
	}
}
