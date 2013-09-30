/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Format", propOrder = {
    "name",
    "template"
})


public class Format implements Serializable{

	private static final long serialVersionUID = 1L;

	@XmlTransient
	private String id;
	
    @XmlElement(required = true)
     private String name;
    
    @XmlElement(required = true)
    
    private String template;
    
    @XmlTransient    
    private ReportMetadata metadata;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String value) {
		template = value;
	}

	public ReportMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ReportMetadata metadata) {
		this.metadata = metadata;
	}
}
