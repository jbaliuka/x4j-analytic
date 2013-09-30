/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TableStyle;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfRenderer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameter;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.RenderingParameters;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class TableRowProcessor extends PdfRowProcessor {

    private float rowHeigth;

    public TableRowProcessor(PdfContainer node) {
		super(node);
	}

	@Override
	public void process(XLXContext context) throws Exception {
        scheduleRowNodeForDrawing((PdfContext) context);
        processChildren(context);
		
	}

    private void scheduleRowNodeForDrawing(PdfContext context) throws Exception {
		rowNode.setTableId(context.getTableId());
		rowHeigth = rowNode.estimateHeight(new RenderingContext(context, RenderingParameters.empty()));
		context.prepareNewLine(rowHeigth + PdfRenderer.ROW_MARGIN);
		RenderingParameters params = new RenderingParameters(RenderingParameter.ROW_HEIGHT, rowHeigth);
		context.drawLater(rowNode, params);
    }

    private void processChildren(XLXContext context) throws Exception {
        TableStyle tableStyle = getCurrentTableStyle(context);
        for (Node child : rowNode.getChildren()) {
            setChildHeightAndColor(tableStyle, child);
            child.process(context);
        }
    }

    private TableStyle getCurrentTableStyle(XLXContext context) {
        return context.findTableStyle(context.getTableId());
    }

    private void setChildHeightAndColor(TableStyle tableStyle, Node child) {
        if (child instanceof PdfCellNode) {
            ((PdfCellNode) child).setRowHeigth(rowHeigth);
            ((PdfCellNode) child).setFillColor(((PdfCellNode) child).getColor(tableStyle));
        }
    }

}
