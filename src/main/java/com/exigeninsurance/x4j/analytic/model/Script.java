/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.model;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Script", propOrder = {
		"name",
		"parameters",
		"type",
		"text"
})

public class Script {
	
	@XmlElement(required = false)
	private String type;
	@XmlElement(required = true)
	private String name;
	@XmlElement(required = true)
	private String text;
	@XmlElement
    @XmlList
    private List<String> parameters;
	
	
	@XmlTransient    
    private ReportMetadata metadata;


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ReportMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(ReportMetadata metadata) {
		this.metadata = metadata;
	}
	public List<String> getParameter() {
		return  (parameters == null ? Collections.<String>emptyList() : parameters);
	}
	public void setParameter(List<String> parameter) {
        parameters = parameter;
	}

}
