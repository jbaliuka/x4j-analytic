/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.util;

import static com.exigeninsurance.x4j.analytic.xlsx.utils.CellExpressionParser.parseExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.ConcatExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.ConstantExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class CellExpressionParserTest {
	
	@Test
	public void testTextParse() throws Exception {
		String testValue = "test";
		XLSXExpression expr = parseExpression(testValue);
		assertTrue(expr instanceof ConstantExpression);
		assertEquals(testValue, ((ConstantExpression)expr).getStr());
	}
	
	@Test
	public void testExpressionParse() throws Exception {
		String expression = "expression";
		String testValue = "${" + expression + "}";
		XLSXExpression expr = parseExpression(testValue);
		assertTrue(expr instanceof SimpleExpression);
		assertEquals(expression, expr.toString());
	}
	
	@Test
	public void testTextAndExpressions() throws Exception {
		String expression = "expression";
		String text = "text";
		String testValue = "${" + expression + "}" + text;
		XLSXExpression expr = parseExpression(testValue);
		assertTrue(expr instanceof ConcatExpression);
		ConcatExpression concat = (ConcatExpression) expr;
		assertEquals(2, concat.getExpressions().size());
		
		assertTrue(concat.getExpressions().get(0) instanceof SimpleExpression);
		assertEquals(expression, concat.getExpressions().get(0).toString());
		
		assertTrue(concat.getExpressions().get(1) instanceof ConstantExpression);
		assertEquals(text, ((ConstantExpression)concat.getExpressions().get(1)).getStr());
	}
	
	@Test
	public void testDollarSignWithoutExpression() throws Exception {
		String testValue = "$text";
		XLSXExpression expr = parseExpression(testValue);
		assertTrue(expr instanceof ConstantExpression);
		assertEquals(testValue, ((ConstantExpression)expr).getStr());
	}
	
	@Test
	public void testComplex() throws Exception {
		String testValue = "&L$E8&C${report_name}";
		XLSXExpression expr = parseExpression(testValue);
		assertTrue(expr instanceof ConcatExpression);
		ConcatExpression concat = (ConcatExpression) expr;
		assertEquals(3, concat.getExpressions().size());
		
		assertTrue(concat.getExpressions().get(0) instanceof ConstantExpression);
		assertEquals("&L", ((ConstantExpression)concat.getExpressions().get(0)).getStr());
		
		assertTrue(concat.getExpressions().get(1) instanceof ConstantExpression);
		assertEquals("$E8&C", ((ConstantExpression)concat.getExpressions().get(1)).getStr());
		
		assertTrue(concat.getExpressions().get(2) instanceof SimpleExpression);
		assertEquals("report_name", concat.getExpressions().get(2).toString());
	}
	
	@Test
	public void testNullExpressions() throws Exception {
		String testKey = "test";
		String testValue = null;
		
		XLSXExpression expr = parseExpression("${test}");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(testKey, testValue);
		ReportContext reportContext = new ReportContext(null);
		XLXContext context = new XLXContext(null, new XSSFWorkbook().createSheet(), reportContext , null);
        assertNull(expr.evaluate(context));
		
	}
}
