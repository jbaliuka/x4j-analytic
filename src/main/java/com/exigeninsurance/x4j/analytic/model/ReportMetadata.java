/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.exigeninsurance.x4j.analytic.util.StringUtils;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="template" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameter" type="{http://www.exigen.com/reportMetadata}Parameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="data-set" type="{http://www.exigen.com/reportMetadata}DataSet"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"parent",
		"id",
		"name",
		"category",
		"description",
		"template",
		"formats",
		"delivery",
		"folder",
		"file",
		"templates",
		"cube",
		"parameter",
		"query",
		"script",
		"attribute"
})
@XmlRootElement(name = "report-metadata")


public class ReportMetadata implements Serializable{

	public static final String DOWNLOAD = "download";
	
	public static final String VERSION = "build.number";

	private static final long serialVersionUID = 1L;
	
	private static final HashMap<String, String> MIME_TYPE =new HashMap<String, String>();
	
	public static final List<String> DELIVERY_TYPES = Arrays.asList("download","mail","efolder");

	static{
		MIME_TYPE.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		MIME_TYPE.put("xls", "application/msexcel");
		MIME_TYPE.put("html", "text/html");
		MIME_TYPE.put("pdf", "application/pdf");
		MIME_TYPE.put("csv", "text/plain");
		MIME_TYPE.put("xml", "text/xml");
		MIME_TYPE.put("zip", "application/zip");
	}

	@XmlElement(required = false)
	private String parent;

	@XmlElement(required = true)
	private String id;

	@XmlElement(required = true)
	private String name;

	@XmlElement(required = true)
	private String category;

	@XmlElement(required = true)
	private String description;

	@XmlElement(required = false)
	private String template;

    @XmlElement(required = false)
    private String folder;

    @XmlElement(required = false)
    private String file;

	@XmlElement(required = false)
	private String formats;

	@XmlElement(required = false)
	private String delivery;

	@XmlElement(required = false)
	private Templates templates;
	private String cube;
	private List<Parameter> parameter;
	private List<Query> query;
	private List<Script> script;

	private List<Attribute> attribute;

	@XmlTransient
	private ReportMetadata parentMetadata;

	/**
	 * Gets the value of the id property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setId(String value) {
        id = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setName(String value) {
        name = value;
	}

	/**
	 * Gets the value of the category property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getCategory() {
		if(category == null){
			if(parentMetadata != null){
				return parentMetadata.getCategory();
			}
		}
		return category;
	}
    
   
	/**
	 * Sets the value of the category property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setCategory(String value) {
        category = value;
	}

	/**
	 * Gets the value of the description property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setDescription(String value) {
        description = value;
	}

	/**
	 * Gets the value of the template property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getTemplate() {
		return template;
	}

	
	public boolean supports(String outputFormat) {
		String cleanedUpFormat = outputFormat == null ? "" : outputFormat.trim().toLowerCase();

		if (templates != null) {
			List <Format> formats = templates.getFormat();
			for (Format format : formats) {
				if (format.getName().trim().equalsIgnoreCase(cleanedUpFormat)) {
					return true;
				}
			}
		}
		return parseFormats().contains(outputFormat);
	}
	
	public String getTemplate(String outputFormat) {
		if(outputFormat == null){
			throw new IllegalArgumentException("format is required");
		}
		String cleanedUpFormat = outputFormat.trim().toLowerCase();
		if (templates != null) {
			List <Format> formats = templates.getFormat();
			for (Format format : formats) {
				if (format.getName().trim().equalsIgnoreCase(cleanedUpFormat)) {
					return format.getTemplate();
				}
			}

		}

		return template;
	}

	/**
	 * Sets the value of the template property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setTemplate(String value) {
        template = value;
	}

	/**
	 * Gets the value of the formats property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getFormats() {
		return formats;
	}

	/**
	 * Sets the value of the formats property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setFormats(String value) {
        formats = value;
	}
	/**
	 * Gets the value of the parameter property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the parameter property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getParameter().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Parameter }
	 * 
	 * 
	 */
	public List<Parameter> getParameter() {
		ArrayList<Parameter> list = new ArrayList<Parameter>();
		list.addAll(getSelfParameter());
		if (parent != null && parentMetadata != null) {
			list.addAll(parentMetadata.getParameter());
		}
		Collections.sort(list, new Comparator<Parameter>() {
			public int compare(Parameter o1, Parameter o2) {
				int i1 = o1.getOrd() == null ? -1 : o1.getOrd();
				int i2 = o2.getOrd() == null ? -1 : o2.getOrd();
				return i1 - i2;
			}
		});
		return list;
	}

	public List<Parameter> getSelfParameter() {
		if (parameter == null) {
			parameter = new ArrayList<Parameter>();
		}      
		for(Parameter p : parameter){
			p.setMetadata(this);
		}
		return parameter;
	}

	public List<Query> getSelfQuery() {
		if (query == null) {
			query = new ArrayList<Query>();
		}
		for(Query q : query){
			q.setMetadata(this);
		}

		return query;
	}
	public List<Query> getQuery() {
		ArrayList<Query> list = new ArrayList<Query>();
		list.addAll(getSelfQuery());
		if (parent != null && parentMetadata != null) {
			list.addAll(parentMetadata.getQuery());
		}
		return list;
	}

	public List<Attribute> getAttributes() {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.addAll(getSelfAttribute());
		if (parent != null && parentMetadata != null) {
			list.addAll(parentMetadata.getAttributes());
		}
		return list;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getBuildNumber(){
		for(Attribute attr : getAttributes()){
			if(VERSION.equals(attr.getName())){
				return attr.getValue();
			}
		}

		return null;
	}

	public String getTemplateFormat(String output) {
		String templateName = getTemplate(output);
		return templateName.substring(templateName.lastIndexOf('.') + 1).toLowerCase();
	}

	public Query getQueryByName(String name){
		for(Query q : getQuery() ){
			if(q.getName().equals(name)){
				return q;
			}
		}
		return null;
	}
	
	public Script getScriptByName(String name){
		for(Script s : getScript() ){
			if(s.getName().equals(name)){
				return s;
			}
		}
		return null;
	}

	public static String getMimeType(String format){
		return (MIME_TYPE.get(format) == null) ? "application/binary" : MIME_TYPE.get(format);
	}

	public Parameter getParameterByName(String name) {
		for (Parameter p : getParameter()) {
			if(name.equals(p.getName())){
				return p;
			}
		}

		return null;
	}

	public String getCube() {
		return cube;
	}

	public void setCube(String cube) {
		this.cube = cube;
	}

	public void setParameter(List<Parameter> parameter) {
		this.parameter = parameter;
	}

	public void setQuery(List<Query> query) {
		this.query = query;
	}

	public void setAttribute(List<Attribute> attribute) {
		this.attribute = attribute;
	}

	public List<String> parseFormats() {
		return split(formats);
	}

	private List<String> split(String s) {
		List<String> result = new ArrayList<String>();
		if (!StringUtils.isEmpty(s)) {
			String [] strings = s.split(",");
			for (int i = 0; i < strings.length; i++) {
				result.add(strings[i].trim().toLowerCase());
			}
		}
		return result;
	}

	public Attribute getAttributeByName(String name) {
		for(Attribute att: getAttributes()){
			if(att.getName().equals(name)){
				return att;
			}
		}		
		return null;
	}

	public List<Attribute> getSelfAttribute() {
		if (attribute == null) {
			attribute = new ArrayList<Attribute>();
		}
		for(Attribute a : attribute){
			a.setMetadata(this);
		}
		return attribute;
	}
	
	public List<Script> getSelfScript() {
		if (script == null) {
			script = new ArrayList<Script>();
		}
		for(Script a : script){
			a.setMetadata(this);
		}
		return script;

	}

	public void setPrentMetadata(ReportMetadata parentMetadata) {
		this.parentMetadata = parentMetadata;
	}

	public ReportMetadata getParentMetaData(){
		return parentMetadata;
	}

	public Templates getTemplates() {
		return templates;
	}

	public void setTemplates(Templates templates) {
		this.templates = templates;
	}

	public String getFolder() {
		if(folder == null){
			if(parentMetadata != null){
				return parentMetadata.getFolder();
			}
		}
		
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List<Script> getScript() {
		ArrayList<Script> list = new ArrayList<Script>();
		list.addAll(getSelfScript());
		if (parent != null && parentMetadata != null) {
			list.addAll(parentMetadata.getScript());
		}
		return list;
	}
	
	public boolean deliverable(String deliveryChannel){
		return delivery == null ? true : parseDelivery().contains(deliveryChannel);
	}

	public void setScript(List<Script> script) {
		this.script = script;
	}

	public String getDelivery() {
		return delivery;
	}
	
	

	
		
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	
	
	public List<String> getDeliveryTypes() {
		if(delivery == null){
			return DELIVERY_TYPES;
		}
		return parseDelivery();
	}

	private List<String> parseDelivery() {
		return split(delivery);
	}

	public void setDelivery(String delivery) {
		if(!DELIVERY_TYPES.containsAll(split(delivery))){
			throw new IllegalArgumentException(" list of "  + delivery + " contains unsupported types, supported: " + DELIVERY_TYPES);
		}
        this.delivery = delivery;
	}
	
	
	
}