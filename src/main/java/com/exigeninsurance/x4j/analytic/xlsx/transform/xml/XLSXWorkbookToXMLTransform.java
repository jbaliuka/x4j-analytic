
/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xml;

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

/**
 * @author jbaliuka
 *
 */

public class XLSXWorkbookToXMLTransform extends BaseTransform {
	
	private static final Logger log = LoggerFactory.getLogger(XLSXWorkbookToXMLTransform.class);
	private OutputStream output;
	
	public XLSXWorkbookToXMLTransform(){
		
	}
	 
	public XLSXWorkbookToXMLTransform(OutputStream output) {
		this.output = output;
	}

	protected File createWorkbookFile() throws Exception {
		return IOUtils.createTempFile("xml");
	}

	protected void doProcess(XLSXWorkbook workbook, ReportContext reportContext, File saveTo)
			throws Exception {
		OutputStream out = saveTo == null ? output : new BufferedOutputStream(
				new FileOutputStream(saveTo));
		try {
			try {
				XMLProcessor processor = new XMLProcessor(workbook, out);
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
