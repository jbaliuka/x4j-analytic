/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components;

import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfPictureNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class PictureProcessor implements Processor {

    private PdfPictureNode pictureNode;

    public PictureProcessor(PdfPictureNode pictureNode) {
        this.pictureNode = pictureNode;
    }

    @Override
    public void process(XLXContext context) throws Exception {
        PdfContext pdfContext = (PdfContext) context;
        pdfContext.createAndCacheImage(pictureNode, pictureNode.getPictureData().getPictureData());
		RenderingParameters params = new RenderingParameters(RenderingParameter.PICTURE_DATA, pictureNode.getPictureData());
		pdfContext.drawLater(pictureNode, params);
    }
}
