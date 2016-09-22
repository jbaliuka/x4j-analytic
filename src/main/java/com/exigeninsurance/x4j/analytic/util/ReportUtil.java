/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.model.Script;


public class ReportUtil {

	
	private static final String REPORT_METADATA_XSD = "/reportMetadata.xsd";
	private static final String MODEL_PACKAGE = "com.exigeninsurance.x4j.analytic.model";
	public static final JexlEngine ENGINE = new JexlEngine();

    private ReportUtil() {

    }

	public static void validate(InputStream is,final List<String> messages){
		try {
			JAXBContext	jc = JAXBContext.newInstance( MODEL_PACKAGE );
			Unmarshaller u = jc.createUnmarshaller();			
			try{	
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				InputStream in = ReportUtil.class.getResourceAsStream(REPORT_METADATA_XSD);
				try{
					Source locationOfMySchema = new StreamSource(in);
					Schema schema = schemaFactory.newSchema(locationOfMySchema );                 
					u.setSchema(schema);
					ValidationEventHandler handler = new ValidationEventHandler(){

						public boolean handleEvent(ValidationEvent event) {
							if(event.getSeverity() != ValidationEvent.WARNING){
								messages.add(event.getMessage());
							}
							return true;
						}

					};
					u.setEventHandler(handler );
					u.unmarshal( is );
				}finally{
					in.close();
				}

			}finally{
				is.close();
			}

		} catch (Exception e) {
			messages.add(e.getMessage());
		}
	}

		
	/**
	 * Reads ReportMetadata from XML stream
	 * @param is
	 * @return
	 */
	public static ReportMetadata unmarshal(InputStream is ){
		try {
			JAXBContext	jc = JAXBContext.newInstance( MODEL_PACKAGE );
			Unmarshaller u = jc.createUnmarshaller();
			return (ReportMetadata) u.unmarshal( is );			
		} catch (Exception e) {
			throw new ReportException(e);
		}
	}
	/**
	 * Substitutes string with specified parameters
	 * @param str
	 * @param variables
	 * @return
	 */

	

	/**
	 * Marshals report metadata object to XML
	 * @param reportMetadata
	 * @param out
	 */
	public static void marshal(ReportMetadata reportMetadata,OutputStream out ){
		try {
			JAXBContext	jc = JAXBContext.newInstance( MODEL_PACKAGE );
			Marshaller m = jc.createMarshaller();
			m.marshal(reportMetadata, new StreamResult(out) );
		} catch (Exception e) {
			throw new ReportException(e);
		}
	}



	

	public static org.apache.commons.jexl2.Script compile(Script s) {
		List<String> parameter = s.getParameter();
		return ReportUtil.ENGINE.createScript(s.getText(), null, parameter.toArray(new String[parameter.size()]));
	}


	public static Expression createExpression(String expression) {		
		return ENGINE.createExpression(expression);
	}
}
