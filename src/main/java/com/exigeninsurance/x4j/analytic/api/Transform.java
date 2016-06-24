/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;

import java.io.File;
import java.io.InputStream;


/**
 * Transform implementation is specific for every  report format, default implementations support xlsx, csv, html, xml and PDF.
 * Custom implementations might add support for json or replace default.  
 * @author jbaliuka
 *
 */


public interface Transform {
	
	/**
	 * X4jEngine delegates template processing this method
	 * @param reportContext  input
	 * @param  template is any file, it can use custom template language
	 * @param saveTo output
	 * @throws Exception
	 */
    void process(ReportContext reportContext, InputStream template, File saveTo) throws Exception;
	
	void setDataProvider(ReportDataProvider dataProvider);
	
	void setTemplateProvider(TemplateResolver resolver);

}