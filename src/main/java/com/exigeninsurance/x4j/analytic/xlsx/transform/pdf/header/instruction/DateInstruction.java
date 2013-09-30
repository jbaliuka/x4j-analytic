/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import java.text.DateFormat;
import java.util.Date;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;


public class DateInstruction extends TextInstruction {

	public DateInstruction(String contents) {
		super(contents);
	}

	@Override
	public String evaluateContents(PdfContext context) {
		
		return DateFormat.getDateTimeInstance().format(new Date());
	}

}
