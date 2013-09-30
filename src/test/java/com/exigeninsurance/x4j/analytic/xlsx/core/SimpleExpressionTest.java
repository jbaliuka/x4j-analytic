/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class SimpleExpressionTest {

	@Test
	public void testEmpty() throws Exception {
		SimpleExpression e = new SimpleExpression("");
		assertNull(e.evaluate(null));
		e = new SimpleExpression(null);
		assertNull(e.evaluate(null));
	}

	@Test
	public void testNpe() throws Exception {
		SimpleExpression e = new SimpleExpression("1");
		assertNull(e.evaluate(null));
	}

	@Test
	public void testSimple() throws Exception {
		SimpleExpression e = new SimpleExpression("test");

		ReportContext reportContext = new ReportContext(null);
		Map<String, Object> params = reportContext.getParameters();
		params.put("test", "value");
		
		XLXContext context = new XLXContext(null, new XSSFWorkbook().createSheet(), reportContext, new ByteArrayOutputStream());

		assertEquals("value", e.evaluate(context));
	}
}
