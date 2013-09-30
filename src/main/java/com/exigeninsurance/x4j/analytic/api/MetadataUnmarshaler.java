package com.exigeninsurance.x4j.analytic.api;

import java.io.InputStream;

import com.exigeninsurance.x4j.analytic.model.ReportMetadata;

/**
 * Meta data parsing strategy, default implementation uses JAXB and predefined XML Schema.
 * ReportMetadata is a hierarchy and implementation should resolve and unmarshal all of parents.    
 * @author jbaliuka
 *
 */


public interface MetadataUnmarshaler {
	
	ReportMetadata unmarshal(InputStream is);

}
