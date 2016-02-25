/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.cell;

import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.MergedRegion;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;


public class WrappingCellHeigthEstimator extends PdfCellEstimator {

    private float itemHeight;
    private List<String> items;

    public WrappingCellHeigthEstimator(PdfCellNode node) {
		super(node);
	}

	@Override
	public float estimate(RenderingContext context) {
		PdfContext pdfContext = context.getPdfContext();
		String value = formatValue(context);
        itemHeight = node.getMaxFontHeight();
        
        float width=0;
        if(node.isMerged(context.getPdfContext())){        	
        	MergedRegion associatedRegion = node.getMergedRegion(pdfContext);			
        	width = node.getParent().getMergedRegionWidth(context, node, associatedRegion);			
        }else {
        	width = node.estimateWidth(context);
        }
        
        items = node.splitCell(value,width, pdfContext.getMargins());

        return items.isEmpty() ? 0f : calculateHeight();
	}

    private float calculateHeight() {
        return sumRowHeights() + margins();
    }

    private float margins() {
        return (items.size() - 1) * PdfRenderer.ROW_MARGIN;
    }

    private float sumRowHeights() {
        float height = 0f;
        for (int i = 0; i < items.size(); i++) {
            height += itemHeight;
        }
        return height;
    }

}
