/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class MeasuringRowProcessor extends PdfRowProcessor {

    public MeasuringRowProcessor(PdfContainer node) {
        super(node);
    }

    @Override
    public void process(XLXContext context) throws Exception {
        PdfContext pdfContext = (PdfContext) context;

        for (Node child : rowNode.getChildren()) {
            child.process(context);
        }

        pdfContext.reportRowWidth(rowNode);
    }
}
