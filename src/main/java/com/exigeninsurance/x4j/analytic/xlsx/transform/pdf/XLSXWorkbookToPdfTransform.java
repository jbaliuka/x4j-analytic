/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.transform.BaseTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;


public class XLSXWorkbookToPdfTransform extends BaseTransform {

	private OutputStream output;

	public XLSXWorkbookToPdfTransform(){

	}

	public XLSXWorkbookToPdfTransform(OutputStream output){
		this.output = output;
	}

	public void doProcess(XLSXWorkbook workbook, ReportContext reportContext, File saveTo) 
			throws Exception {
				OutputStream out = saveTo == null ? output : new BufferedOutputStream(
						new FileOutputStream(saveTo));
				try {

					PdfProcessor processor = new PdfProcessor(workbook);
					processor.setDataProvider(getDataProvider());
					processor.setTemplateProvider(getTemplateProvider());
					
					processor.setFormatProvider(getFormatProvider());
					try {
						processor.processWorkbook(reportContext);
						processor.saveDocument(saveTo);
					} finally {
						processor.close();
					}
				}finally {
					out.close();
				}

	}

	protected File createWorkbookFile() throws Exception {

		return IOUtils.createTempFile("pdf");
	}
}
