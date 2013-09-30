/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Processor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class PdfPictureNode extends Node implements DrawablePdfElement {
	
	private Picture pictureData;
    private PdfContainer parent;
    private Processor processor;

    private float width;
    private float height;

	public PdfPictureNode(XSSFSheet sheet, Picture picture) {
		super(sheet);
        pictureData = picture;
        width = PDFHelper.emuToPdfUnits(pictureData.getEmuWidth());
        height = PDFHelper.emuToPdfUnits(pictureData.getEmuHeight());
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
		processor.process(context);
	}

	public void draw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
        float offset = pdfContext.getLeftHorizontalMargin();
        for (int i = 0; i < pictureData.getFromCol(); i++) {
            offset += columnWidth(pdfContext, i);
        }
		pdfContext.drawImageAt(this, offset, pdfContext.getY() - height + parent.getHeigth(), width, height);
		pdfContext.movePointerBy(width, 0);
	}

    private float columnWidth(PdfContext context, int i) {
        String col = CellReference.convertNumToColString(i);
        return context.getMargins() + (context.isExpanded(col) ?
                context.getColumnWidth(col) : PDFHelper.excelToPixel(getSheet().getColumnWidth(i)));
    }

    public float estimateWidth(RenderingContext context) {
		return width;
	}

	public float estimateHeight(RenderingContext context) {
		return 0f;
	}

	public void notify(PdfContext context) {
		
	}

	public Picture getPictureData() {
		return pictureData;
	}

	@Override
	public void setParent(PdfContainer element) {
	    this.parent = element;
	}

	@Override
	public PdfContainer getParent() {
		return parent;
	}

	@Override
	public float getHeigth() {
		return 0;
	}

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}
