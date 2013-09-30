/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.header.instruction;

import org.apache.poi.xssf.usermodel.XSSFPictureData;

import com.exigeninsurance.x4j.analytic.xlsx.transform.Picture;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.PDFHelper;


public class PictureInstruction extends Instruction {

    private String sectionCode;

    public PictureInstruction(String contents, String sectionCode) {
        super(contents);
        this.sectionCode = sectionCode;
    }

    @Override
    public void process(PdfContext context) {
        XSSFPictureData pictureData = context.getHeaderFooterPicture(sectionCode).getPictureData();
        context.createAndCacheImage(this, pictureData);
    }

    @Override
    public void draw(RenderingContext context) throws Exception {
		PdfContext pdfContext = context.getPdfContext();
		Picture headerFooterPicture = pdfContext.getHeaderFooterPicture(sectionCode);
        float height = PDFHelper.emuToPdfUnits(headerFooterPicture.getEmuHeight());
        float width = PDFHelper.emuToPdfUnits(headerFooterPicture.getEmuWidth());
		pdfContext.drawImage(this, height, width, height);
		pdfContext.movePointerBy(width, 0);
    }

    @Override
    public float estimateWidth(RenderingContext context) {
        Picture headerFooterPicture = context.getPdfContext().getHeaderFooterPicture(sectionCode);
        return PDFHelper.emuToPdfUnits(headerFooterPicture.getEmuWidth());
    }

    @Override
    public float estimateHeight(RenderingContext context) {
        Picture headerFooterPicture = context.getPdfContext().getHeaderFooterPicture(sectionCode);
        return PDFHelper.emuToPdfUnits(headerFooterPicture.getEmuHeight());
    }

    @Override
    public void notify(PdfContext context) {

    }
}
