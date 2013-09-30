/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;


public class RightHeaderFooterSection extends HeaderFooterSection {

	public RightHeaderFooterSection(String contents, String headerFooter) {
		super(contents, headerFooter);
        sectionPosition = "R";
		super.prepareForRendering();
	}

    @Override
    public void doDraw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
		float startY = pdfContext.getY();
        final float startX = pdfContext.getPageWidth() - pdfContext.getRightHorizontalMargin();
        for (Instruction line : getDrawingInstructions()) {
            startY -= line.estimateHeight(context);
			pdfContext.movePointerTo(startX - line.estimateWidth(context), startY);
            line.draw(context);
        }
    }
}
