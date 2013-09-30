/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import org.apache.commons.jexl2.Expression;

import com.exigeninsurance.x4j.analytic.util.ReportUtil;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public final class SimpleExpression implements XLSXExpression {


	
	private Expression jexlExpresssion;

	public SimpleExpression(String expr) {
		if(expr != null && !expr.trim().isEmpty()){
            jexlExpresssion = ReportUtil.createExpression( expr );
		}
	}


	

	public Object evaluate(XLXContext context) {

		 if(context == null){
			return null;
		 }

		if(jexlExpresssion == null){
			return null;
		}
        return jexlExpresssion.evaluate(context.getExpresionContext());


	}

	@Override
	public String toString() {		
		return jexlExpresssion == null ? "<null expression>" : jexlExpresssion.toString();
	}

	


}
