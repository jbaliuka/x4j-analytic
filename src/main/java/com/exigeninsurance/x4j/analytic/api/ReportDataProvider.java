/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.api;

import java.sql.SQLException;

import com.exigeninsurance.x4j.analytic.model.Query;
/**
 * Interface to execute SQL queries. Custom implementation might use csv file or DBMS specific adapter.
 * Default implementation relies on named parameter support by JDBC driver and uses CallbaleStatment to execute any query.
 * Implementation should release any external resources after query execution: Connections, Cursors, ...   
 * @author jbaliuka
 *
 */

public interface ReportDataProvider {
/**
 * Executes query and delegates result processing for  callback
 * @param context
 * @param callback
 * @throws SQLException
 */
	void execute(Query query,ReportContext context, ReportDataCallback callback)
	throws SQLException;	
	
}