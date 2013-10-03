/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.api.ReportDataCallback;
import com.exigeninsurance.x4j.analytic.api.ReportDataProvider;
import com.exigeninsurance.x4j.analytic.model.Query;


public class MockReportDataProvider implements ReportDataProvider{

	private final Map<String, ResultSet> rsMap = new HashMap<String, ResultSet>();
	private ResultSet defaultRs;

	public MockReportDataProvider(ResultSet rs){
		
		defaultRs = rs;
	}
	
	public MockReportDataProvider(Object ...objects ){
		for(int i = 0; i < objects.length; i +=2 ){
			ResultSet rs = (ResultSet)objects[i + 1];
			assert rs != null;
			rsMap.put((String)objects[i],rs);
		}
	}


	public void execute(Query query,ReportContext context,
			ReportDataCallback callback) throws SQLException {

		
		ResultSet rs = rsMap.get(query.getName());
		if(rs == null){
		 rs = defaultRs;	
		}
		
		try {
			callback.process(new ResultSetWrapper(rs));
		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
            rs.close();
        }

	}

	


}