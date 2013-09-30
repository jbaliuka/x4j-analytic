/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class SetNode extends Node {
	
	private final String contextVariableName;
	private XLSXExpression expression;

	public SetNode(XSSFSheet sheet, String contextVariableName, XLSXExpression expression) {
		super(sheet);
		this.contextVariableName = contextVariableName;
		this.expression = expression;
	}

	@Override
	public void process(XLXContext context) throws Exception {
		Object value = getExpression().evaluate(context);
		context.getExpresionContext().set(contextVariableName, value);
	}

	public String getContextVariableName() {
		return contextVariableName;
	}

	public XLSXExpression getExpression() {
		return expression;
	}

	public void setExpression(XLSXExpression expression) {
		this.expression = expression;
	}
	
	
}
