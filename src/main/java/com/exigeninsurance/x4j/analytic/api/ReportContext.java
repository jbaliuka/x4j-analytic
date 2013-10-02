/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
 */

package com.exigeninsurance.x4j.analytic.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.CursorManager;
/**
 * Reports context encapsulates  parameters and meta data
 * 
 */

public class ReportContext {

	
	private static final String OUTPUT_FORMAT = "outputFormat";
	private static final String REPORT_METADATA = "reportMetadata";
	private ReportMetadata metadata;
	private Locale locale = Locale.getDefault();
	private CursorManager cursorManager;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private String outputFormat = "xlsx";

	public ReportContext(ReportMetadata metadata) {
		super();
		this.metadata = metadata;
		parameters.put(REPORT_METADATA, metadata);
		parameters.put(OUTPUT_FORMAT, outputFormat);
	}

	public ReportMetadata getMetadata() {
		return metadata;
	}
	

	public void setMetadata(ReportMetadata metadata) {
		 this.metadata = metadata;
	}
	

	public Map<String, Object> getParameters() {
		return parameters;
	}

	
	public void setCursorManager(CursorManager cursorManager) {
		this.cursorManager = cursorManager;
	}

	public CursorManager getCursorManager() {
		return cursorManager;
	}
	

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale){
		this.locale = locale;
	}



	public String getOutputFormat() {
		return outputFormat;
	}



	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	
}
