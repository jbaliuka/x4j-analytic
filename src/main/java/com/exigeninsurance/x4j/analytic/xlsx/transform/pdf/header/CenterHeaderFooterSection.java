
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction.Instruction;


public class CenterHeaderFooterSection extends HeaderFooterSection {

	public CenterHeaderFooterSection(String contents, String headerFooter) {
		super(contents, headerFooter);
        sectionPosition = "C";
		super.prepareForRendering();
	}

    @Override
    public void doDraw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
        float startY = pdfContext.getY();
        final float middleX = pdfContext.getPageWidth() / 2;
        for (Instruction line : getDrawingInstructions()) {
            startY -= line.estimateHeight(context);
			pdfContext.movePointerTo(middleX - line.estimateWidth(context) / 2, startY);
            line.draw(context);
        }
    }
}
