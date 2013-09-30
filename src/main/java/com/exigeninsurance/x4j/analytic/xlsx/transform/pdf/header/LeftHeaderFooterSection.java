/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;


public class LeftHeaderFooterSection extends HeaderFooterSection {
	
	public LeftHeaderFooterSection(String contents, String headerFooter) {
		super(contents, headerFooter);
        sectionPosition = "L";
		super.prepareForRendering();
	}
	
    @Override
    public void doDraw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
        for (Instruction line : getDrawingInstructions()) {
			pdfContext.movePointerToNewLine(line.estimateHeight(context));
            line.draw(context);
        }
    }
}
