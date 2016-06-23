/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.transform.BaseTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;


final public class XLSXWorkbookToHTMLTransaform extends BaseTransform {

	private static final Logger log = LoggerFactory.getLogger(XLSXWorkbookToHTMLTransaform.class);
	private OutputStream output;
	
	public XLSXWorkbookToHTMLTransaform(){
		
	 }
	 
	 public XLSXWorkbookToHTMLTransaform(OutputStream output){
			this.output = output;
	 }


	public void doProcess(XLSXWorkbook workbook, ReportContext reportContext, File saveTo) throws Exception {

			OutputStream out = saveTo == null ? output
					: new BufferedOutputStream(new FileOutputStream(saveTo));
			try {
				try {
					HTMLProcessor processor = new HTMLProcessor(workbook, out);
					processor.setDataProvider(getDataProvider());
					processor.setTemplateProvider(getTemplateProvider());
					
					processor.setFormatProvider(getFormatProvider());
					List<String> savedParts = Collections.emptyList();
					processor.processSheets(reportContext, savedParts);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					throw e;
				}

			} finally {
				out.close();
			}


	}

	protected File createWorkbookFile(ReportContext reportContext,
			InputStream in, File saveTo) throws Exception {
		return IOUtils.createTempFile("html");
	}

}
