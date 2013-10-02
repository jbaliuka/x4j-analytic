/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Link;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.model.ReportMetadata;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.util.ResultSetWrapper;
import com.exigeninsurance.x4j.analytic.xlsx.transform.BaseTransform;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TransformationTest;
import com.exigeninsurance.x4j.analytic.xlsx.transform.csv.XLSXWorkbookToCsvTransform;
import com.exigeninsurance.x4j.analytic.xlsx.utils.XSSFSheetHelper;

public class TransformTest extends TransformationTest {
	
	@Before
	public void setup() throws IOException {
		super.setup();
	}

    @After
    public void teardown() {
        super.teardown();
    }

	final ResultSet rs45 = MockResultSet.create(
			cols  (     "A", "B", "C", "D" ),
			data( 	row(1, 2, 3, 4),
					row(1, 2, 3, 4), 
					row(5, 6, 7, 8),
					row(5, 6, 7, 8),
					row(5, 6, 7, 8)
			)
	); 
	
	final ResultSet tableMacro = MockResultSet.create(
			cols  (     "TEST_COL", "TEST_COL1"  ),
			data( 	row("TEST VALUE 1", "TEST VALUE 2" ),
					row("TEST VALUE 3", "TEST VALUE 4" ),
					row("TEST VALUE 5", "TEST VALUE 6" ),
					row(new Date() + "", "TEST VALUE 6" )
			)
	);

	final int stressSize = 500*1000;
	final ResultSet stress = MockResultSet.create(
			cols  (     "A", "B", "C", "D" ),
			data( 	row(5, "123456", new Date(), "ABCD")
			)
			,stressSize);


	private void testTables(BaseTransform transform, ResultSet rs) throws Exception {
		transform.setDataProvider(new MockReportDataProvider(rs));

		InputStream in = getClass().getResourceAsStream("/testTables.xlsx");
		assertNotNull(in);

		try{

			ReportMetadata metadata = new ReportMetadata();
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
			
			metadata.setQuery(list);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			ReportContext reportContext = new ReportContext(metadata);

			File saveTo = new File("transform1.xlsx");
			transform.process(reportContext, in, saveTo);
			
			assertTrue(saveTo.delete());

		}finally{
			in.close();
		}

	}


	@Test
	public void testStress() throws Exception{
		if(System.getProperty("XLSXStress") != null){
			long start = System.currentTimeMillis();
			testTables( new XLSXWorkbookTransaform(),stress);
			System.out.println( "XLSX stress with " + (stressSize*2) + " records takes " + (System.currentTimeMillis() - start)/1000 + " s." );			
			start = System.currentTimeMillis();
			testTables( new XLSXWorkbookToCsvTransform(),stress);
			System.out.println( "CSV stress with " + (stressSize*2) + " records takes " + (System.currentTimeMillis() - start)/1000 + " s." );
		}
	}

	@Test
	public void testTaskTemplate()throws Exception{

		XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
		
		ResultSet userInfo = MockResultSet.create(		
				cols  ( "request_label","request_by"), 	data( row("User:","TEST"	) )
		);

		ResultSet taskReinstatedLate = MockResultSet.create(		
				cols  ( "renewal_effective_date",	"priority",	"assigned_to",	"policy_reference",	"customer_name",	"due_date",	"escalation_date",	"last_performer"),
				data( row("renewal_effective_date",	"priority",	"assigned_to",	"policy_reference",	"customer_name",	"due_date",	"escalation_date",	"last_performer"	) 
				)
		);

		assertTrue( userInfo != null );
		assertTrue( taskReinstatedLate != null );
		
        dataProvider = new MockReportDataProvider("userInfo", userInfo, "taskReinstatedLate", taskReinstatedLate);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/taskReinstatedLate.xlsx");
		assertNotNull(in);

		try{
			
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("userInfo");
			q1.setSql(q1.getName());
			list.add(q1);

			Query q2 = new Query();
			q2.setName("taskReinstatedLate");
			q2.setSql(q2.getName());
			list.add(q2);
			
			metadata.setQuery(list);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			ReportContext reportContext = createContext(parameters);
			Map<String,Query> sqlQueries = new HashMap<String, Query>();
			sqlQueries.put(q1.getName(), q1);
			sqlQueries.put(q2.getName(), q2);
			
			reportContext.getParameters().put("sqlQuery",sqlQueries );

			File saveTo = new File("taskTest.xlsx");
			transform.process(reportContext, in, saveTo);
			FileInputStream fin = new FileInputStream(saveTo);
			try{
				XSSFWorkbook book = new XSSFWorkbook(fin);
				XSSFSheet sheet = book.getSheetAt(0);
				XSSFRow row = sheet.getRow(4);
				XSSFCell cell = row.getCell(2);
				assertEquals("TEST",  cell.getStringCellValue());
			}finally{
				fin.close();
			}

			assertTrue(saveTo.delete());

		}finally{
			in.close();
		}

	}

	

	@Test
	public void testSubtotals() throws Exception {

		XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
        dataProvider = new MockReportDataProvider(rs45);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/subtotals.xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
			
			metadata.setQuery(list);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			ReportContext reportContext = createContext(parameters);

			File saveTo = new File("subtotals.xlsx");
			transform.process(reportContext, in, saveTo);
			
			FileInputStream fin = new FileInputStream(saveTo);
			try{
				XSSFWorkbook book = new XSSFWorkbook(fin);
				XSSFSheet sheet = book.getSheetAt(0);
				XSSFRow row = sheet.getRow(4);
				XSSFCell cell = row.getCell(0);
				assertEquals(5.0,  cell.getNumericCellValue(), 0.1);
			}finally{
				fin.close();
			}

			assertTrue(saveTo.delete());

		}finally{
			in.close();
		}
	}
	
	
	
	@Test
	public void testGrouping() throws Exception {
		String firstAgency = "FirstAgency";
		String secondAgency = "SecondAgency";
		ResultSet rs = MockResultSet.create(cols("Agency", "Producer", "C"), data(
				row(firstAgency, "1", "A"),
				row(firstAgency, "2", "A"),
				row(secondAgency, "3", "A"),
				row(secondAgency, "4", "A")));
		
		String [] expectedValues = {
				firstAgency, 
					"1", 
					"2", 
				secondAgency, 
					"3", 
					"4"};
		
		test(rs, "rows", "testGrouping", expectedValues);
	}
	
	@Test
	public void testNestedGroups() throws Exception {
		ResultSet rs = MockResultSet.create(cols("A", "B", "C"), data(
				row("A", "AA", "1"),
				row("A", "AA", "2"),
				row("A", "BB", "3"),
				row("A", "BB", "4"),
				row("B", "CC", "5"),
				row("B", "CC", "6"),
				row("B", "DD", "7"),
				row("B", "DD", "8")));
		
		String [] expectedValues = {
				"A", 
					"AA", 
						"1", 
						"2", 
					"BB", 
						"3", 
						"4",
				"B",
					"CC", 
						"5", 
						"6", 
					"DD", 
						"7", 
						"8"
		};
		
		test(rs, "rows", "testNestedGroups" , expectedValues);
	}
	
	@Test
	public void testGroupSubtotals() throws Exception {
		ResultSet rs = MockResultSet.create(cols("A", "B"), data(
				row("A", 1),
				row("A", 2),
				row("A", 3),
				row("A", 4),
				row("B", 5),
				row("B", 6),
				row("B", 7),
				row("B", 8)));
		
		String [] expectedValues = {
				"A", 
					"10.0",
				"B",
					"26.0"
		};
		
		test(rs, "rows", "testGroupSubtotals", expectedValues);
	}
	
	@Test
	public void testHyperlinks() throws Exception {
		
		String linkLabel = "linkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		String linkAddress = "http://www.google.lt";
		
		Link link = new Link(linkLabel, linkAddress);
		
		ResultSet rs = MockResultSet.create(cols("LINK"), data(
				row(link)));
		
		XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
		transform.setDataProvider(new MockReportDataProvider(rs));

		InputStream in = getClass().getResourceAsStream("/testHyperlinks.xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>(); 
			
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
						
			metadata.setQuery(list);
			
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("link", link);
			ReportContext reportContext = createContext(parameters);

			File saveTo = new File("testHyperlinksResults.xlsx");
			transform.process(reportContext, in, saveTo);
			
			FileInputStream fin = new FileInputStream(saveTo);
			try{
				XSSFWorkbook workbook = new XSSFWorkbook(fin);
				XSSFCell cell = workbook.getSheetAt(0).getRow(0).getCell(0);
				
				assertEquals(XSSFCell.CELL_TYPE_FORMULA, cell.getCellType());
				String cellFormula = cell.getCellFormula();
				String expectedForuma = "HYPERLINK(\"" + linkAddress + "\",\"" + linkLabel + "\")";
				assertEquals(expectedForuma, cellFormula);

                XSSFCellStyle cellStyle = cell.getCellStyle();
                XSSFFont font = cellStyle.getFont();
                assertEquals("Calibri", font.getFontName());
                assertEquals(11, font.getFontHeightInPoints());
                
            }finally{
				fin.close();
			}

			assertTrue(saveTo.delete());

		}finally{
			in.close();
		}
	}
	
	@Test
	public void testMergedCells() throws Exception {
		ResultSet rs = MockResultSet.create(cols("A"), data(
				row("A"),
				row("A")));
		
		XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
        dataProvider = new MockReportDataProvider(rs);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/testMergedCells.xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
			
			metadata.setQuery(list);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("rows", ResultSetWrapper.wrap(rs));
			ReportContext reportContext = createContext(parameters);

			File saveTo = new File("testMergedCellsResult.xlsx");
			transform.process(reportContext, in, saveTo);
			
			FileInputStream fin = new FileInputStream(saveTo);
			try{
				XSSFWorkbook book = new XSSFWorkbook(fin);
				XSSFSheet sheet = book.getSheetAt(0);
				CellRangeAddress firstRegion = sheet.getMergedRegion(0);
				testRegion(firstRegion, 0, 0, 0, 1);
				
				CellRangeAddress firstForRegion = sheet.getMergedRegion(1);
				testRegion(firstForRegion, 0, 2, 2, 2);
				
				CellRangeAddress secondForRegion = sheet.getMergedRegion(2);
				testRegion(secondForRegion, 0, 3, 2, 3);
				
				CellRangeAddress lastRegion = sheet.getMergedRegion(3);
				testRegion(lastRegion, 0, 5, 1, 6);
			}finally{
				fin.close();
			}

			assertTrue(saveTo.delete());

		}finally{
			in.close();
		}
	}

    @Test
    public void testRepeatingRows() throws Exception {
        ResultSet rs = MockResultSet.create(cols("A"), data(
                row("A")), 4);

        InputStream in = getClass().getResourceAsStream("/testRepeatingRows.xlsx");
        assertNotNull(in);

        try{
            XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
            dataProvider = new MockReportDataProvider(rs);
            transform.setDataProvider(dataProvider);

            ArrayList<Query> list = new ArrayList<Query>();
            Query q1 = new Query();
            q1.setName("Table1");
            list.add(q1);

            metadata.setQuery(list);

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("list", ResultSetWrapper.wrap(rs));
            ReportContext reportContext = createContext(parameters);

            File saveTo = new File("testRepeatingRowsResult.xlsx");
            try {
                transform.process(reportContext, in, saveTo);

                FileInputStream fin = new FileInputStream(saveTo);
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(fin);
                    int[] repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 0);
                    int[] expected = new int[] {1, 2};
                    assertArrayEquals(expected, repeatingRows);

                    repeatingRows = XSSFSheetHelper.getRepeatingRows(workbook, 1);
                    expected = new int[] {6, 7};
                    assertArrayEquals(expected, repeatingRows);
                } finally {
                    fin.close();
                }

            } finally {
                assertTrue(saveTo.delete());
            }
        } finally {
            in.close();
        }
    }
	
	private void testColumnValues(XSSFSheet sheet, int column, int startingRow, String ... expectedValues) {
		int rowOffset = 0;
		for (String value : expectedValues) {
			XSSFRow row = sheet.getRow(startingRow + rowOffset);
			XSSFCell cell = row.getCell(column);
			String text = null;
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_STRING:
				text = cell.getStringCellValue();
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				text = String.valueOf(cell.getNumericCellValue());
			default:
				break;
			}
			assertEquals(value, text);
			rowOffset++;
		}
	}
	
	private void testRegion(CellRangeAddress region, int firstCol, int firstRow, int lastCol, int lastRow) {
		assertNotNull(region);
		assertEquals(firstCol, region.getFirstColumn());
		assertEquals(firstRow, region.getFirstRow());
		assertEquals(lastCol, region.getLastColumn());
		assertEquals(lastRow, region.getLastRow());
	}
	
	private void test(ResultSet resultSet, String rowsObject, String template, String [] expectedValues) throws Exception {
		XLSXWorkbookTransaform transform = new XLSXWorkbookTransaform();
        dataProvider = new MockReportDataProvider(resultSet);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/" + template + ".xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
			
			metadata.setQuery(list);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(rowsObject, ResultSetWrapper.wrap(resultSet));
			ReportContext reportContext = createContext(parameters);

			File saveTo = new File(template + "Result.xlsx");
			transform.process(reportContext, in, saveTo);
			
			FileInputStream fin = new FileInputStream(saveTo);
			try{
				XSSFWorkbook book = new XSSFWorkbook(fin);
				XSSFSheet sheet = book.getSheetAt(0);
				testColumnValues(sheet, 0, 0, expectedValues);
			}finally{
				fin.close();
				assertTrue(saveTo.delete());
			}

			

		}finally{
			in.close();
		}
	}
}
