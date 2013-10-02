/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

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
import org.junit.Test;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.model.Query;
import com.exigeninsurance.x4j.analytic.util.MockReportDataProvider;
import com.exigeninsurance.x4j.analytic.util.MockResultSet;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.TransformTest;

public class XLSXWorkbookToCsvTransformTest extends TransformTest {

	@Before
	public void setup() throws IOException {
		super.setup();
	}

    @After
    public void teardown() {
        super.teardown();
    }

	@Test
	public void testProcess() throws Exception {
		
		ResultSet rs = MockResultSet.create(cols("A", "B", "C", "D"), data(row(
				"\"a\"", "3.21", "2.0", "1.0")));
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		XLSXWorkbookToCsvTransform transform = new XLSXWorkbookToCsvTransform(out);
        dataProvider = new MockReportDataProvider(rs);
        transform.setDataProvider(dataProvider);

		InputStream in = getClass().getResourceAsStream("/basiclong.xlsx");
		assertNotNull(in);

		try{
			ArrayList<Query> list = new ArrayList<Query>();

			Query q2 = new Query();
			q2.setName("Table4");
			list.add(q2);
			
			metadata.setQuery(list);

            ReportContext reportContext = createContext(new HashMap<String, Object>());
			
			transform.process(reportContext, in, null);
			
			String columns = "\"A\",\"B\",\"C\",\"D\"";
			String doubleQuotes = "\"\"\"a\"\"\"";
			
			String result = out.toString("UTF8");
			
			
			assertTrue("No line breaks found", result.contains("\n"));
			assertTrue("Strings not surrounded by quotes", result.contains(columns));
			assertTrue("Incorrect Number formatting", result.indexOf("3.21") > 0);
			assertTrue("Incorrectly escaped", result.contains(doubleQuotes));
			
		}finally{
			in.close();
		}
	}
}
