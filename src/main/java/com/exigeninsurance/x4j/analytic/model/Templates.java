/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Templates", propOrder = {
    "format"
})

public class Templates implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@XmlTransient	
	private String id;
	
	private List <Format> format;
    @XmlTransient  
    private ReportMetadata metadata;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setMetadata(ReportMetadata metadata) {
		this.metadata = metadata;
	}

	public ReportMetadata getMetadata() {
		return metadata;
	}

	public List<Format> getFormat() {
		return format;
	}

	public void setFormat(List<Format> outputFormat) {
        format = outputFormat;
	}
}
