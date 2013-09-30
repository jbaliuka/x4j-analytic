/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/



package com.exigeninsurance.x4j.analytic.xlsx.transform;

import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.api.TemplateResolver;
import com.exigeninsurance.x4j.analytic.xlsx.core.localization.FormatProvider;


public class WorkbookProcessor {
	
	private ReportDataProvider dataProvider;
	private TemplateResolver templateProvider;	
	private FormatProvider formatProvider;
	
	public ReportDataProvider getDataProvider() {
		return dataProvider;
	}

	public TemplateResolver getTemplateProvider() {
		return templateProvider;
	}

	public void setDataProvider(ReportDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setTemplateProvider(TemplateResolver templateProvider) {
		this.templateProvider = templateProvider;
	}

	public FormatProvider getFormatProvider() {
		return formatProvider;
	}

	public void setFormatProvider(FormatProvider formatProvider) {
		this.formatProvider = formatProvider;
	}
	
	

}
