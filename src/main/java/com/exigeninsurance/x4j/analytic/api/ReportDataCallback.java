/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;

import java.sql.ResultSet;
/**
 * Data processor interface for application specific logic implementation
 * @author jbaliuka
 *
 */

public interface ReportDataCallback {

	/**
	 * Safe processing, data provider closes resources
	 * @param rs
	 * @throws Exception
	 */
	void process(ResultSet rs) throws Exception;

}
