/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import java.awt.Color;

import org.apache.poi.ss.usermodel.Font;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;


public abstract class TextInstruction extends Instruction {

	public TextInstruction(String contents) {
		super(contents);
	}

    public abstract String evaluateContents(PdfContext context);

	public void draw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
		pdfContext.setTextOptions(getFontSize(),  super.getPDFont(getFontStyle()), Font.U_NONE);
		pdfContext.drawTextAtPointer(evaluateContents(pdfContext),Color.BLACK);
	}

	public float estimateWidth(RenderingContext context)  {
		return PDFHelper.findCellTextLength(getFontSize(), super.getPDFont(getFontStyle()), evaluateContents(context.getPdfContext()));
		
	}

	public float estimateHeight(RenderingContext context) {
		return PDFHelper.findMaxFontHeigth(super.getPDFont(getFontStyle()), getFontSize());
	}

	public void notify(PdfContext context)  {

	}
}
