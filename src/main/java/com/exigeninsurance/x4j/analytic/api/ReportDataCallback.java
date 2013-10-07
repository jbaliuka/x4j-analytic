/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;


/**
 * Data processor interface for application specific logic implementation
 * @author jbaliuka
 *
 */

public interface ReportDataCallback {

	/**
	 * Call back for safe processing, data provider closes resources
	 * @param rs result set wrapper
	 * @throws Exception
	 */
	void process(Cursor rs) throws Exception;

}
