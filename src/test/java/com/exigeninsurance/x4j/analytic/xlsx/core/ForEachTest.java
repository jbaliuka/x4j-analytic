/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.core;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.XLSXExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.ForEachNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;

public class ForEachTest {

	private XSSFSheet sheet;
	private Map<String,Object> parameters;
	private ReportContext reportContext;
	private XLXContext context;

	@Before
	public void setup() {
		XSSFWorkbook wb = new XSSFWorkbook();
		sheet = wb.createSheet();		
		reportContext = new ReportContext(null);
		parameters = reportContext.getParameters();
		context = new XLXContext(null, sheet, reportContext, null);
	}

	@Test
	public void testForeachGroupingBasic () throws Exception {
		Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row("A", 1, 3, 1),
				row("A", 2, 3, 2),
				row("B", 3, 3, 3),
				row("B", 4, 3, 4))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setGroupingColumn("a");
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		
		TestingNode rootTestNode = new TestingNode("A");
		rootNode.getChildren().add(rootTestNode);
		rootNode.getChildren().add(aNode);
		
		TestingNode aTestNode = new TestingNode("B");
		aNode.getChildren().add(aTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(4, aTestNode.getSize());
		verifyResults(aTestNode, 1, 2, 3, 4);
		
		assertEquals(2, rootTestNode.getSize());
		verifyResults(rootTestNode, "A", "B");
	}
	
	@Test
	public void testForeachGroupingNested () throws Exception {
		Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(1, 11, 3, 2),
				row(1, 22, 3, 3),
				row(1, 22, 3, 4),
				row(2, 33, 3, 5),
				row(2, 33, 3, 6),
				row(2, 44, 3, 7),
				row(2, 44, 3, 8))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("b");
		aNode.setGroupDataObject("group_B");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		
		TestingNode rootTestNode = new TestingNode("A");
		rootNode.getChildren().add(rootTestNode);
		rootNode.getChildren().add(aNode);
		
		TestingNode aTestNode = new TestingNode("B");
		aNode.getChildren().add(aTestNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("D");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(8, bTestNode.getSize());
		verifyResults(bTestNode, 1, 2, 3, 4, 5, 6, 7, 8);
		
		assertEquals(4, aTestNode.getSize());
		verifyResults(aTestNode, 11, 22, 33, 44);
		
		assertEquals(2, rootTestNode.getSize());
		verifyResults(rootTestNode, 1, 2);
	}
	
	@Test
	public void testForeachOneElementGroups () throws Exception {
		Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(2, 22, 3, 2),
				row(3, 33, 3, 3),
				row(4, 44, 3, 4))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("B");
		aNode.setGroupDataObject("group_B");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		
		TestingNode rootTestNode = new TestingNode("A");
		rootNode.getChildren().add(rootTestNode);
		rootNode.getChildren().add(aNode);
		
		TestingNode aTestNode = new TestingNode("B");
		aNode.getChildren().add(aTestNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("D");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(4, bTestNode.getSize());
		verifyResults(bTestNode, 1, 2, 3, 4);
		
		assertEquals(4, aTestNode.getSize());
		verifyResults(aTestNode, 11, 22, 33, 44);
		
		assertEquals(4, rootTestNode.getSize());
		verifyResults(rootTestNode, 1, 2, 3, 4);
	}
	
	@Test
	public void testForeachLast () throws Exception {
		Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(1, 11, 3, 2),
				row(1, 22, 3, 3),
				row(1, 22, 3, 4),
				row(2, 33, 3, 5),
				row(2, 33, 3, 6),
				row(2, 44, 3, 7),
				row(2, 44, 3, 8))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("B");
		aNode.setGroupDataObject("group_B");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		bNode.setVar("B_row");
		
		rootNode.getChildren().add(aNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("group_B.last(\"B\") ? B_row.D : \"\"");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);

		rootNode.process(context);
		
		assertEquals(8, bTestNode.getSize());
		verifyResults(bTestNode, "", 2, "", 4, "", 6, "", 8);
	}
	
	@Test
	public void testForeachFirst () throws Exception {
        Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(1, 11, 3, 2),
				row(1, 22, 3, 3),
				row(1, 22, 3, 4),
				row(2, 33, 3, 5),
				row(2, 33, 3, 6),
				row(2, 44, 3, 7),
				row(2, 44, 3, 8))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("B");
		aNode.setGroupDataObject("group_B");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		bNode.setVar("B_row");
		
		rootNode.getChildren().add(aNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("group_B.first(\"B\") ? B_row.D : \"\"");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(8, bTestNode.getSize());
		verifyResults(bTestNode, 1,"", 3,"",5, "",7, "");
	}
	
	@Test
	public void testForeachLastOnMiddleFor () throws Exception {
        Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(1, 11, 3, 2),
				row(1, 22, 3, 3),
				row(1, 22, 3, 4),
				row(2, 33, 3, 5),
				row(2, 33, 3, 6),
				row(2, 44, 3, 7),
				row(2, 44, 3, 8))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("B");
		aNode.setGroupDataObject("group_B");
		aNode.setVar("A_row");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		bNode.setVar("B_row");
		
		rootNode.getChildren().add(aNode);
		
		TestingNode aTestNode = new TestingNode("group_A.first(\"A\") ? A_row.B : \"\"");
		aNode.getChildren().add(aTestNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("group_B.last(\"B\") ? B_row.D : \"\"");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(8, bTestNode.getSize());
		verifyResults(bTestNode, "", 2, "", 4, "", 6, "", 8);
		
		assertEquals(4, aTestNode.getSize());
		verifyResults(aTestNode, 11, "", 33, "");
	}
	
	@Test
	public void testForeachWithoutParams () throws Exception {
        Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A", "B", "C", "D"), data(
				row(1, 11, 3, 1),
				row(1, 11, 3, 2),
				row(1, 22, 3, 3),
				row(1, 22, 3, 4),
				row(2, 33, 3, 5),
				row(2, 33, 3, 6),
				row(2, 44, 3, 7),
				row(2, 44, 3, 8))));
		
		ForEachNode rootNode = new ForEachNode(null);
		rootNode.setRows(new SimpleExpression("rows"));
		rootNode.setGroupingColumn("A");
		rootNode.setGroupDataObject("group_A");
		ForEachNode aNode = new ForEachNode(null);
		aNode.setRows(new SimpleExpression("group_A"));
		aNode.setGroupingColumn("B");
		aNode.setGroupDataObject("group_B");
		aNode.setVar("A_row");
		ForEachNode bNode = new ForEachNode(null);
		bNode.setRows(new SimpleExpression("group_B"));
		bNode.setVar("B_row");
		
		rootNode.getChildren().add(aNode);
		
		TestingNode aTestNode = new TestingNode("group_A.first() ? A_row.B : \"\"");
		aNode.getChildren().add(aTestNode);
		aNode.getChildren().add(bNode);
		
		TestingNode bTestNode = new TestingNode("group_B.last() ? B_row.D : \"\"");
		bNode.getChildren().add(bTestNode);

		parameters.put("rows", cursor);
		
		rootNode.process(context);
		
		assertEquals(8, bTestNode.getSize());
		verifyResults(bTestNode, "", 2, "", 4, "", 6, "", 8);
		
		assertEquals(4, aTestNode.getSize());
		verifyResults(aTestNode, 11, "", 33, "");
	}
	
	private void verifyResults(TestingNode node, Object ...exptectedValues) {
		for (int i = 0; i < exptectedValues.length; i++) {
			assertEquals(exptectedValues[i], node.getValue(i));
		}
	}
	
	private class TestingNode extends Node {
		
		private final List<Object> values = new ArrayList<Object>();
		private XLSXExpression expression;

		public TestingNode(String expression) {
			super(null);
			this.expression = new SimpleExpression(expression);
		}

		@Override
		public void process(XLXContext context) throws Exception {
			values.add(expression.evaluate(context));
		}
		
		public Object getValue(int index) {
			return values.get(index);
		}
		
		public int getSize() {
			return values.size();
		}
	}
}
