/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.row;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContainer;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.PdfContext;
import com.exigeninsurance.x4j.analytic.xlsx.transform.pdf.components.Notifier;


public class HeaderRowNotifier implements Notifier {
	
	private final PdfContainer rowNode;

	public HeaderRowNotifier(PdfContainer rowNode) {
		this.rowNode = rowNode;
	}

	public void notify(PdfContext context) {
		if (sameTable(context)) {
			try {
				context.repeat(rowNode);
			} catch (Exception e) {
				throw new ReportException(e);
			}
		}		
	}

    private boolean sameTable(PdfContext context) {
        return rowNode.getTableId() == context.getTableId();
    }

}
