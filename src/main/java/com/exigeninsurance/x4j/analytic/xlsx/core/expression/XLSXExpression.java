/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core.expression;


import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public interface XLSXExpression {

	
	Object evaluate(XLXContext context) throws Exception;

	

}