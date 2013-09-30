/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public final class IfNode extends Node{

	public IfNode( XSSFSheet sheet) {
		super( sheet );		
	}

	private XLSXExpression condition;

	public void setCondition(XLSXExpression condition) {
		this.condition = condition;
	}

	public XLSXExpression getCondition() {
		return condition;
	}

	@Override
	public void process(XLXContext context) throws Exception {
		if (evaluate(context)){
			super.process(context);
		}
	}

	public boolean evaluate(XLXContext context) throws Exception {
		Object result = condition.evaluate(context);
		return result instanceof Boolean ? (Boolean) result : false;
	}
}
