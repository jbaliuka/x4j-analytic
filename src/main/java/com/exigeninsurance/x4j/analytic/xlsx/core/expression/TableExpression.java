/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import com.exigeninsurance.x4j.analytic.api.ReportException;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public final class TableExpression implements XLSXExpression {
	private final String tableName;

	public TableExpression(String tableName) {
		this.tableName = tableName;
	}

	public Object evaluate(XLXContext context)
	throws Exception {
		Object param = context.getReportContext().getParameters().get(tableName);
		if(param != null){
			return param;
		}else {
			for( Query q : context.getReportContext().getMetadata().getQuery() ){
				if(q.getName().equals(tableName)){
					if( context.getTemplateProvider() != null){
						return context.getReportContext().getMetadata().getQueryByName(tableName);
					}else {
						return q; 
					}
				} 
			}
		}
		throw new ReportException("query " + tableName + " not found") ;
	}

	
}