/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.node;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;


public class EvalNode extends Node {
	
	private final XLSXExpression expression;

	public EvalNode(XSSFSheet sheet, XLSXExpression expression) {
		super(sheet);
		this.expression = expression;
	}
	
	@Override
	public void process(XLXContext context) throws Exception {
		expression.evaluate(context);
	}

}
