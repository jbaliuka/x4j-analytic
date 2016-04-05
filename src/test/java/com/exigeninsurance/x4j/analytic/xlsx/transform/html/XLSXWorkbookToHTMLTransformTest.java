/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.html;

import static com.exigeninsurance.x4j.analytic.util.MockResultSet.cols;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.data;
import static com.exigeninsurance.x4j.analytic.util.MockResultSet.row;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.xlsx.transform.TransformationTest;

public class XLSXWorkbookToHTMLTransformTest extends TransformationTest {
	
	final ResultSet rs45 = MockResultSet.create(
			cols  (     "A", "B", "C", "D" ),
			data( 	row(5, 6, 7, 8),
					row(5, 6, 7, 8), 
					row(5, 6, 7, 8),
					row(5, 6, 7, 8),
					row(5, 6, 7, 8)
			)
	);
	
	@Before
	public void parseFiles() throws IOException {
		super.setup();
	}

    @After
    public void teardown() {
        super.teardown();
    }

	
	@Test
	public void testProcess() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		XLSXWorkbookToHTMLTransaform transform = new XLSXWorkbookToHTMLTransaform(out);
        dataProvider = new MockReportDataProvider(rs45);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/testTables.xlsx");
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

            ReportContext reportContext = createContext(new HashMap<String, Object>());
			
			transform.process(reportContext, in, null);
			
			String str = out.toString("UTF8");
			
			
			assertTrue( str.indexOf("<table>") > 0 );
			assertTrue( str.indexOf("</table>") > 0 );
			assertTrue( str.indexOf("id=\"workbook\"") > 0 );
			assertTrue( str.indexOf("A1") > 0 );
			
			
			
		}finally{
			in.close();
		}

	}
	
	@Test	
	public void testColors() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XLSXWorkbookToHTMLTransaform transform = new XLSXWorkbookToHTMLTransaform(out);
        dataProvider = new MockReportDataProvider(rs45);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/tables.xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>(); 
			Query q1 = new Query();
			q1.setName("Table1");
			list.add(q1);

			Query q2 = new Query();
			q2.setName("Table2");
			list.add(q2);
			
			Query q3 = new Query();
			q3.setName("Table3");
			list.add(q3);
			
			metadata.setQuery(list);

            ReportContext reportContext = createContext(new HashMap<String, Object>());

			transform.process(reportContext, in, null);
			
			String text = out.toString("UTF8");
			assertTrue(text.indexOf("bgcolor=#dbe5f1") > 0);
			assertTrue(text.indexOf("bgcolor=#FFFFFF") > 0);
			assertTrue(text.indexOf("bgcolor=#4f81bd") > 0);

		}finally{
			in.close();
		}
	}
}
