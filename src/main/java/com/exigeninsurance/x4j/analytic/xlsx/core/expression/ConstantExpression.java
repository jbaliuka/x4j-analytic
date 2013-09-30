/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/



package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class ConstantExpression implements XLSXExpression {

	private Object str;

	public ConstantExpression(String str){
		this.str = str;
	}
	
	public Object evaluate(XLXContext context) throws Exception {
		
		return str;
	}

	

	public Object getStr() {
		return str;
	}

	public void setStr(Object str) {
		this.str = str;
	}
	
	
	
	
}