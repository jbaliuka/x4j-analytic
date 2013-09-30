/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;

import java.util.ArrayList;
import java.util.List;

import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class ConcatExpression implements XLSXExpression {

	private List<XLSXExpression> expressions = new ArrayList<XLSXExpression>();

	public Object evaluate(XLXContext context) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for( XLSXExpression e : expressions){
			Object val = e.evaluate(context);
			if (val != null) {
				buffer.append(val);
			}
		}
		return buffer.toString().trim();
	}

	public ConcatExpression append(XLSXExpression e){
		expressions.add(e);
		return this;
	}

	

	public List<XLSXExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<XLSXExpression> expressions) {
		this.expressions = expressions;
	}
}