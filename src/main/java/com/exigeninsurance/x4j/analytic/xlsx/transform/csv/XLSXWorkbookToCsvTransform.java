/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.util.IOUtils;
import com.exigeninsurance.x4j.analytic.xlsx.transform.BaseTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;


public class XLSXWorkbookToCsvTransform extends BaseTransform {
	
	private static final Logger log = LoggerFactory.getLogger(XLSXWorkbookToCsvTransform.class);
	private OutputStream output;
	
	public XLSXWorkbookToCsvTransform(){
		
	}
	 
	public XLSXWorkbookToCsvTransform(OutputStream output) {
		this.output = output;
	}

	protected File createWorkbookFile() throws Exception {
		return IOUtils.createTempFile("csv");
	}

	protected void doProcess(XLSXWorkbook workbook,ReportContext reportContext, File saveTo)
			throws Exception {
		OutputStream out = saveTo == null ? output : new BufferedOutputStream(
				new FileOutputStream(saveTo));
		try {
			try {
				CsvProcessor processor = new CsvProcessor(workbook, out);
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

}
