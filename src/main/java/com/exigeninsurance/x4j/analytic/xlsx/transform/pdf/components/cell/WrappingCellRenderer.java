/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import java.io.IOException;
import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class WrappingCellRenderer extends AbstractCellRenderer {

  
	public WrappingCellRenderer(PdfCellNode node) {
		super(node);       
	}

    @Override
    protected void drawText(RenderingContext context, Object value) throws IOException {
		PdfContext pdfContext = context.getPdfContext();
		float width = textArea.getWidth();
		if (value != null) {
            String string = node.formatValue(pdfContext, value);
            List<String> lines = node.splitCell(string, width, 0);
            float lineHeight = node.getMaxFontHeight();
            float y = pdfContext.getY() + findVerticalOffset(context, lines);
            setTextOptions(pdfContext);
            for (int i = lines.size() - 1; i > -1; i--) {
                String item = lines.get(i);
                float x = textArea.getLowerLeft().getX() + findHorizontalOffset(item) + findStartingDrawingPoint(width);
				pdfContext.drawText(item,node.getTextColor(), x, y);
                y += lineHeight + PdfRenderer.ROW_MARGIN;
            }
        }

		pdfContext.movePointerBy(drawingArea.getWidth(), 0);
    }

    @Override
    protected boolean applyFillAndBorders(RenderingContext context) {
        return true;
    }

    

}
