/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
 */

package com.exigeninsurance.x4j.analytic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private String tableStyleName;
	private String pivotStyleName;
	private List<String> styles = new ArrayList<String>();

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

	public String getTableStyleName() {
		return tableStyleName;
	}

	public void setTableStyleName(String tableStyleName) {
		this.tableStyleName = tableStyleName;
	}

	public String getPivotStyleName() {
		return pivotStyleName;
	}

	public void setPivotStyleName(String pivotStyleName) {
		this.pivotStyleName = pivotStyleName;
	}

	public List<String> getStyles() {
		return styles;
	}

	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	
}
