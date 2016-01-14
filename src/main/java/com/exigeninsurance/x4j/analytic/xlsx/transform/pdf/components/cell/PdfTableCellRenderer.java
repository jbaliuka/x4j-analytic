/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import java.io.IOException;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class PdfTableCellRenderer extends AbstractCellRenderer {

	public PdfTableCellRenderer(PdfCellNode node) {
		super(node);
	}

    @Override
    protected void drawText(RenderingContext context, Object value) throws IOException {
		PdfContext pdfContext = context.getPdfContext();
		float colWidth = drawingArea.getWidth();
		if (value != null) {
            String text = node.formatValue(pdfContext, value);
            float x = textArea.getLowerLeft().getX() + findHorizontalOffset(text) + findStartingDrawingPoint(textArea.getWidth());
            float y = textArea.getLowerLeft().getY() + findVerticalOffset(textArea.getHeight());
            setTextOptions(pdfContext);
            pdfContext.drawText(text,node.getTextColor(), x, y);
        }
        pdfContext.movePointerBy(colWidth, 0);
    }

    @Override
    protected boolean applyFillAndBorders(RenderingContext context) {
        return true;
    }
}
