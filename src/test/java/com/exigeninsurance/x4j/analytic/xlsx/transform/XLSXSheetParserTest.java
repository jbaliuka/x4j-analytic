/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.Cursor;
import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;
import com.exigeninsurance.x4j.analytic.xlsx.core.expression.SimpleExpression;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.ForEachNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.IfNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.SetNode;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TextNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXCellNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXRowNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXSheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;

public class XLSXSheetParserTest {

    private XSSFSheet sheet;
    private ReportContext reportContext;
    private XLSXSheetParser parser;
    private Map<String,Object> parameters;

    @Before
    public void setUp() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();        
        reportContext = new ReportContext(new ReportMetadata());
        parameters = reportContext.getParameters();
        parser = new XLSXSheetParser( reportContext);
        parser.setMacroParser(new MacroParser(sheet, new MacroNodeFactoryImpl(sheet)));
    }

    @Test
	public void testParse() throws IOException, InvalidFormatException {
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("test");
		Node root = parser.parse(sheet);
		assertEquals(3, root.getChildren().size());

		Node headNode = root.getChildren().get(0);
		Assert.assertTrue(headNode instanceof TextNode);
		Assert.assertEquals(0, headNode.getChildren().size());
		TextNode rawTextNode = (TextNode)headNode;
		Assert.assertTrue(rawTextNode.getText().equals(""));

		Node tailNode = root.getChildren().get(2);
		Assert.assertTrue(tailNode != null);
		Assert.assertEquals(4, tailNode.getChildren().size());

		rawTextNode = (TextNode)tailNode.getChildren().get(0);
		Assert.assertTrue(rawTextNode.getText().startsWith("</sheetData>"));
		rawTextNode = (TextNode)tailNode.getChildren().get(3);
		Assert.assertTrue(rawTextNode.getText().endsWith("</worksheet>"));

		Node rowNode = root.getChildren().get(1);
		Assert.assertTrue(rowNode instanceof XLSXRowNode);
		Assert.assertEquals(1, rowNode.getChildren().size());
		Node cellNode = rowNode.getChildren().get(0);
		Assert.assertTrue(cellNode instanceof XLSXCellNode);
		Assert.assertEquals(0, cellNode.getChildren().size());
	}

    @Test
	public void testSimpleEval() throws Exception {
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("test");
		Node root = parser.parse(sheet);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		parameters.put("language", "en*US");
		ReportContext reportContext = new ReportContext(null);
		XLXContext context = new XLXContext(null,sheet,reportContext,stream);
		Assert.assertEquals(3, root.getChildren().size());
		Node rowNode = root.getChildren().get(1);
		rowNode.process(context);	
		context.flush();
		String expected = "<row r=\"1\">"+
		"<c r=\"A1\" t=\"inlineStr\"><v>test</v></c></row>";
		Assert.assertEquals(expected, new String(stream.toByteArray()));

	}

    @Test
	public void testExprEval() throws Exception {
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("${test}");
		Node root = parser.parse(sheet);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		XLXContext context = new XLXContext(null,sheet,reportContext,stream);
		context.getReportContext().getParameters().put("test", "strValue");		
		Assert.assertEquals(3, root.getChildren().size());
		Node rowNode = root.getChildren().get(1);		
		rowNode.getChildren().get(0).process(context);	
		context.flush();
		String expected = "<c r=\"A1\" t=\"inlineStr\"><v>strValue</v></c>";
		String actual = new String(stream.toByteArray());
		Assert.assertEquals(expected, actual);

	}

    @Test
	public void testConcat() throws Exception {
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("A${test}B");
		Node root = parser.parse(sheet);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		XLXContext context = new XLXContext(null,sheet,reportContext,stream);
		context.getReportContext().getParameters().put("test", "strValue");		
		Assert.assertEquals(3, root.getChildren().size());
		Node rowNode = root.getChildren().get(1);		
		rowNode.getChildren().get(0).process(context);	
		context.flush();
		String expected = "<c r=\"A1\" t=\"inlineStr\"><v>AstrValueB</v></c>";
		String actual = new String(stream.toByteArray());
		Assert.assertEquals(expected, actual);

	}

    
    @Test
	public void testIf() throws Exception {
		createCellsInColumn(0,
				"#if 1==1", 
					"test", 
				"#end");

		Node root = parser.parse(sheet);		
		Assert.assertEquals(3, root.getChildren().size());

		IfNode ifNode = (IfNode) root.getChildren().get(1);

		Assert.assertTrue(ifNode instanceof IfNode);

		Assert.assertEquals(1, ifNode.getChildren().size());

		Node rowNode = ifNode.getChildren().get(0);

		Assert.assertEquals(1, rowNode.getChildren().size());

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		XLXContext context = new XLXContext(null,sheet,reportContext,stream);		

		ifNode.process(context);

		context.flush();
		
		String expected = "<c r=\"A1\" t=\"inlineStr\"><v>test</v></c>";
		String generated = new String(stream.toByteArray()); 
		Assert.assertTrue(generated.indexOf(expected) > 0);

		ifNode.setCondition(new SimpleExpression("1==0"));
		int size = stream.size();
		ifNode.process(context);
		Assert.assertEquals(size, stream.size());
		
		ifNode.setCondition(new SimpleExpression("testNull"));		
		
		Assert.assertFalse(ifNode.evaluate(context));
	}

    @Test
	public void testIfnested() throws Exception {
		createCellsInColumn(0,
				"#if 1==1", 
					"#if 1==1", 
						"test",
					"#end",
				"#end");

		Node root = parser.parse(sheet);		
		Assert.assertEquals(3, root.getChildren().size());

		Node ifNode = root.getChildren().get(1);

		Assert.assertTrue(ifNode instanceof IfNode);

		Assert.assertEquals(1, ifNode.getChildren().size());

		ifNode = ifNode.getChildren().get(0);

		Assert.assertTrue(ifNode instanceof IfNode);

		Assert.assertEquals(1, ifNode.getChildren().size());

		Node rowNode = ifNode.getChildren().get(0);

		Assert.assertEquals(1, rowNode.getChildren().size());
	}

    @Test
	public void testFor() throws Exception {
		createCellsInColumn(0,
                "#for item in items",
                "${item.A}",
                "#end");

		Node root = parser.parse(sheet);		
		Assert.assertEquals(3, root.getChildren().size());

		Node forNode = root.getChildren().get(1);

		Assert.assertTrue(forNode instanceof ForEachNode);

		Assert.assertEquals(1, forNode.getChildren().size());

		Node rowNode = forNode.getChildren().get(0);

		Assert.assertEquals(1, rowNode.getChildren().size());


		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A"), data(
                row("test1"),
                row("test2")
        )));
		
		parameters.put("items", cursor);
		
		XLXContext context = new XLXContext(null,sheet,reportContext,stream);	
		
		forNode.process(context);
		
		context.flush();

		String expected1 = "<c r=\"A1\" t=\"inlineStr\"><v>test1</v></c>";
		String expected2 = "<c r=\"A2\" t=\"inlineStr\"><v>test2</v></c>";
		String generated = new String(stream.toByteArray()); 

		Assert.assertTrue(generated.indexOf(expected1) > 0);
		Assert.assertTrue(generated.indexOf(expected2) > 0);
	}
	
	@Test
	public void testSetMacro() throws Exception {
		createCellsInColumn(0,
				"#for row in rows", 
					"#set current_A = row.a", 
				"#end");

		Node root = parser.parse(sheet);	
		Assert.assertEquals(3, root.getChildren().size());
		Node forNode = root.getChildren().get(1);
		Assert.assertEquals(1, forNode.getChildren().size());
		Assert.assertTrue(forNode.getChildren().get(0) instanceof SetNode);
		SetNode setNode = (SetNode) forNode.getChildren().get(0);
		
		Assert.assertEquals("current_A", setNode.getContextVariableName());
	}

    @Test
	public void testParseForWithGrouping() throws InvalidFormatException, IOException {
		createCellsInColumn(0,
				"#for row in rows group by A", 
					"for A_row in group_A", 
						"${A_row.B}", 
					"#end", 
				"#end");

		Node root = parser.parse(sheet);	
		
		Assert.assertEquals(3, root.getChildren().size());
		
		Node groupNode = root.getChildren().get(1);
		Assert.assertTrue(groupNode instanceof ForEachNode);
	}

    @Test
	public void testIfInFor() throws Exception {
		createCellsInColumn(0,
				"#for item in items", 
					"${item.A}", 
					"#if 1==1", 
						"${item.A}-${item.A}",
					"#end", 
					"#if 2==2", 
						"${item.A}-${item.A}-${item.A}",
					"#end",
					"#if 3==3",
						"${item.A}-${item.A}-${item.A}-${item.A}",
					"#end",
				"#end");


		Node root = parser.parse(sheet);	
		
		Assert.assertEquals(3, root.getChildren().size());

		Node forNode = root.getChildren().get(1);

		Assert.assertTrue(forNode instanceof ForEachNode);

		Assert.assertEquals(4, forNode.getChildren().size());

		Node rowNode = forNode.getChildren().get(0);

		Assert.assertEquals(1, rowNode.getChildren().size());


		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Cursor cursor = ResultSetWrapper.wrap(MockResultSet.create(cols("A"), data(
				row("test")
				)));
		parameters.put("items",cursor);

		
		XLXContext context = new XLXContext(null,sheet,reportContext,stream);	
		
		forNode.process(context);
		
		context.flush();

		String expected = "<v>test</v>";
		
		String generated = new String(stream.toByteArray()); 

		Assert.assertTrue(generated.indexOf(expected) > 0);
		expected = "<v>test-test</v>";
		Assert.assertTrue(generated.indexOf(expected) > 0);
		expected = "<v>test-test-test</v>";
		Assert.assertTrue(generated.indexOf(expected) > 0);
		expected = "<v>test-test-test-test</v>";
		Assert.assertTrue(generated.indexOf(expected) > 0);
	}

    @Test
	public void testIsTable() throws IOException, InvalidFormatException{
		
		ReportContext reportContext = new ReportContext(new ReportMetadata());

		java.io.InputStream is = getClass().getResourceAsStream("/testTables.xlsx");
		Assert.assertNotNull(is);
		
		try{

			XSSFWorkbook workBook = new  XSSFWorkbook(is); 
			SheetParser parser = new XLSXSheetParser( reportContext);
			XSSFSheet sheet = workBook.getSheetAt(0);
			Assert.assertNotNull(sheet);
			Node root = parser.parse(sheet);
			Assert.assertNotNull(root);
			List <Table> tables = parser.getTables();
            Assert.assertEquals(2, tables.size());
            Assert.assertNotNull(parser.isTable(0, 0));
            Assert.assertNotNull(parser.isTable(5, 1));
			
		}finally{
			is.close();
		}
	}

    @Test
	public void testParseTable() throws Exception{
		
		ReportContext reportContext = new ReportContext(new ReportMetadata());

		java.io.InputStream is = getClass().getResourceAsStream("/testTables.xlsx");
		Assert.assertNotNull(is);
		try{

			XSSFWorkbook workBook = new  XSSFWorkbook(is); 
			SheetParser parser = new XLSXSheetParser( reportContext);
			XSSFSheet sheet = workBook.getSheetAt(0);
			Node root = parser.parse(sheet);
			
			Assert.assertEquals(7, root.getChildren().size());
		}finally{
			is.close();
		}

	}

    

    @Test
	public void testEvalNode() throws Exception {
		List<String> testList = new ArrayList<String>();
		parameters.put("list", testList);
		createCellsInColumn(0,
				"#eval list.add('test')");

		Node root = parser.parse(sheet);
		
		int expectedSize = 1 + 2;
		Assert.assertEquals(expectedSize, root.getChildren().size());
		
		XLXContext context = new XLXContext(null, sheet, reportContext, null);
		root.process(context);
		
		Assert.assertEquals(1, testList.size());
		Assert.assertEquals("test", testList.get(0));
	}
	
	@Test
	public void testMergedCells () throws Exception {
		XSSFRow row = sheet.createRow(0);
		row.createCell(0);
		row = sheet.createRow(1);
		row.createCell(0);
		
		row = sheet.createRow(3);
		row.createCell(0);
		row.createCell(1);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
		
		Node root = parser.parse(sheet);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XLXContext context = new XLXContext(null,sheet,reportContext,stream);	
		context.setMergedCells(parser.getMergedCells());
		root.process(context);
		
		context.flush();
		
		String generated = new String(stream.toByteArray()); 
		
		Assert.assertTrue(generated.indexOf("<mergeCells count=\"2\"") > 0);
		Assert.assertTrue(generated.indexOf("A1:A2") > 0);
		Assert.assertTrue(generated.indexOf("A4:B4") > 0);

	}

    private void createCellsInColumn(int column, String ... cellValues) {
		int rowIndex = 0;
		for (String cellValue : cellValues) {
			XSSFRow row = sheet.createRow(rowIndex);
			XSSFCell xssfCell = row.createCell(column);
			xssfCell.setCellValue(cellValue);
			rowIndex++;
		}
	}

}
