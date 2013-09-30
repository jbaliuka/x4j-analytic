/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import java.util.Calendar;
import java.util.Date;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;


public class TimeInstruction extends TextInstruction {

	public TimeInstruction(String contents) {
		super(contents);
	}

	@Override
	public String evaluateContents(PdfContext context) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) context.getReportContext().getParameters().get("current_date"));
		StringBuilder builder = new StringBuilder();
		builder.append(calendar.get(Calendar.HOUR_OF_DAY));
		builder.append(':');
		builder.append(calendar.get(Calendar.MINUTE));
		builder.append(":");
		builder.append(calendar.get(Calendar.SECOND));
		return builder.toString();
		
	}

}
