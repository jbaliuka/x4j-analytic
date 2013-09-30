/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;

import java.io.FileNotFoundException;
import java.io.InputStream;
/**
 *  Template resolver. Default implementation delegates to context class loader.
 *  Custom implementations might use data base or implement resource cache.
 *   
 * @author jbaliuka
 *
 */

public interface TemplateResolver {
	/**
	 * Resolves resource
	 * @param name
	 * @return resource as stream
	 * @throws FileNotFoundException
	 */
	
	InputStream openTemplate(String name) throws FileNotFoundException;
	
}
